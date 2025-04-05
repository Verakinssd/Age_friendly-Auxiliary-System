package com.example.myapplication.square;

import org.apache.commons.text.similarity.LevenshteinDistance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapplication.R;
import com.example.myapplication.chat.ChatAdapter;
import com.example.myapplication.chat.ChatItem;
import com.example.myapplication.chat.ChatListActivity;
import com.example.myapplication.commodity.AddCommodityActivity;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.Contact;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Type;
import com.example.myapplication.home.HomepageActivity;
import com.example.myapplication.mySearchSuggestion;
import com.example.myapplication.profile.ProfileActivity;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommodityListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1; // 定义请求码
    private FloatingSearchView mSearchView;
    private RecyclerView recyclerView;
    private CommodityAdapter commodityAdapter;
    private List<Commodity> commodityList;
    private List<Commodity> commodityListClone;//用于克隆一份商品
    private LevenshteinDistance distance = new LevenshteinDistance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_list);
        LinearLayout typeButtonContainer = findViewById(R.id.type_button_container);
        mSearchView = findViewById(R.id.commodity_search_view);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化商品列表
        commodityList = LitePal.where("number > ?", "0").find(Commodity.class); // 从数据库加载商品
        commodityAdapter = new CommodityAdapter(this, commodityList, REQUEST_CODE); // 创建适配器
        recyclerView.setAdapter(commodityAdapter); // 设置适配器

        commodityListClone = new ArrayList<>(commodityList); // 保存原始列表

        // 设置类型筛选按钮
        Button buttonForAll = new Button(this);
        String text = "All";
        buttonForAll.setText(text);
        buttonForAll.setTag(null);
        buttonForAll.setPadding(16, 8, 16, 8);
        buttonForAll.setOnClickListener(v -> filterCommodities(mSearchView.getQuery(), (Type) v.getTag()));
        // 设置按钮的布局参数，以确保它在父容器的顶部
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP | Gravity.START; // 确保按钮在父容器的最上方，左对齐
        buttonForAll.setLayoutParams(params);
        typeButtonContainer.addView(buttonForAll);
        for (Type type : Type.values()) {
            Button button = new Button(this);
            button.setText(type.name());
            button.setTag(type);
            button.setPadding(16, 8, 16, 8);
            // 类型筛选按钮点击事件
            button.setOnClickListener(v -> filterCommodities(mSearchView.getQuery(), (Type) v.getTag()));
            button.setLayoutParams(params);
            typeButtonContainer.addView(button);
        }

        // 设置搜索框监听
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            if (!oldQuery.isEmpty() && newQuery.isEmpty()) {
                mSearchView.clearSuggestions();
            } else {
                provideSuggestions(newQuery); // 提供建议项
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(String currentQuery) {
                saveSearchHistory(currentQuery);
                filterCommodities(currentQuery, null); // 仅按搜索内容筛选
            }

            @Override
            public void onSuggestionClicked(SearchSuggestion suggestion) {
                mSearchView.setSearchText(suggestion.getBody());
                filterCommodities(suggestion.getBody(), null); // 搜索内容来自建议项
            }
        });

        // 其他按钮点击事件保持不变
        findViewById(R.id.button_messages).setOnClickListener(v -> {
            Intent intent = new Intent(CommodityListActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_profile).setOnClickListener(v -> {
            Intent intent = new Intent(CommodityListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_sell).setOnClickListener(v -> {
            Intent intent = new Intent(CommodityListActivity.this, AddCommodityActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_square).setOnClickListener(v -> {
            Intent intent = new Intent(CommodityListActivity.this, CommodityListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_home).setOnClickListener(v -> {
            Intent intent = new Intent(CommodityListActivity.this, HomepageActivity.class);
            startActivity(intent);
        });
    }


    // 过滤商品列表
    /*private void filter(String text) {
        List<Commodity> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(commodityListClone); // 如果输入为空，返回全部商品
        } else {
            String regex = ".*" + text.replace("", ".*") + ".*";
            Pattern pattern = Pattern.compile(regex);
            for (Commodity item : commodityListClone) {
                Matcher matcher = pattern.matcher(item.getCommodityName());
                int dist = distance.apply(text, item.getCommodityName());
                if (text.length() <= 3 ) {
                    if (dist < 2 || matcher.find()) {
                        filteredList.add(item);
                    }
                } else if (text.length() <= 6) {
                    if (dist < 4 || matcher.find()) {
                        filteredList.add(item);
                    }
                } else {
                    if (dist < 6 || matcher.find()) {
                        filteredList.add(item);
                    }
                }
            }
        }
        //Log.d("Filter", "Filtered List Size1: " + commodityList.size());
        //Log.d("Filter", "Filtered List Size: " + filteredList.size());
        commodityAdapter.updateList(filteredList); // 更新适配器
        //Log.d("Filter", "Filtered List Size2: " + commodityList.size());
    }*/

    private void filterCommodities(String query, Type selectedType) {
        commodityList.clear(); // 清空当前列表

        for (Commodity commodity : commodityListClone) {
            //Log.d("CommodityType", "Commodity Type: " + commodity.getType() + ", Selected Type: " + selectedType);
            boolean matchesQuery = query == null || query.isEmpty() || commodity.getCommodityName().toLowerCase().contains(query.toLowerCase());
            boolean matchesType = selectedType == null || commodity.getType() == selectedType;
            if (matchesQuery && matchesType && commodity.getNumber() > 0) {
                commodityList.add(commodity); // 添加符合条件的商品
            }
        }
        if (commodityList.isEmpty()) {
            Toast.makeText(this, "没有找到符合条件的商品", Toast.LENGTH_SHORT).show();
        }
        commodityAdapter.notifyDataSetChanged(); // 刷新适配器
    }



    /*private void performSearch(String query) {
        // 实现搜索逻辑
        filter(query);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // 重新加载商品列表
            loadCommodityList(); // 调用加载商品列表的方法
        }
    }

    // 加载商品的方法
    private void loadCommodityList() {
        commodityList.clear();
        commodityList = LitePal.where("number > ?", "0").find(Commodity.class); // 从数据库加载商品
        commodityAdapter.updateList(commodityList);
    }

    @SuppressLint("MutatingSharedPrefs")
    private void saveSearchHistory(String query) {
        SharedPreferences preferences = getSharedPreferences("commodity_search_history", MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("history", new HashSet<>());
        if (!query.isEmpty()) {
            historySet.add(query);
        }
        preferences.edit().putStringSet("history", historySet).apply();
    }

    private void provideSuggestions(String query) {
        SharedPreferences preferences = getSharedPreferences("commodity_search_history", MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("history", new HashSet<>());

        List<mySearchSuggestion> suggestions = new ArrayList<>();
        for (String history : historySet) {
            int dist = distance.apply(query, history);
            if (dist < 5) {
                suggestions.add(new mySearchSuggestion(history));
            }
        }
        // 将建议项设置到搜索框
        mSearchView.swapSuggestions(suggestions);
    }

}

