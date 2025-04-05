package com.example.myapplication.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.commodity.AddCommodityActivity;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.Contact;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.home.HomepageActivity;
import com.example.myapplication.mySearchSuggestion;
import com.example.myapplication.notification.NotificationActivity;
import com.example.myapplication.profile.ProfileActivity;
import com.example.myapplication.square.CommodityListActivity;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListActivity extends AppCompatActivity {

    private ListView chatListView;
    private FloatingSearchView mSearchView;
    private LevenshteinDistance distance = new LevenshteinDistance();
    private ChatAdapter chatAdapter; // 你的聊天适配器
    private ArrayList<ChatItem> lastChatList; // 最新聊天列表数据
    private HashMap<String, ArrayList<ChatItem>> chatList = new HashMap<>();
    private String userId = String.valueOf(MainActivity.getCurrentUserId());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        chatListView = findViewById(R.id.chat_list_view);
        mSearchView = findViewById(R.id.floating_search_view);
        //searchBox = findViewById(R.id.search_box);
        // TODO: 搜索功能以及菜单功能仍未完善
//        searchButton = findViewById(R.id.search_button);
//        menuButton = findViewById(R.id.menu_button);

        // 初始化聊天列表
        lastChatList = new ArrayList<>();
        // TODO: 添加聊天数据到 chatList
        // lastChatList.add(new ChatItem(2, "QYC", "你好，有空吗？"));
        // lastChatList.add(new ChatItem(3, "CYF", "关于交易的事..."));
        loadChatItem(userId);

        chatAdapter = new ChatAdapter(this, lastChatList);
        chatListView.setAdapter(chatAdapter);

        // 点击事件
//        searchButton.setOnClickListener(v -> {
//            // TODO: 实现搜索功能
//            String query = searchBox.getText().toString();
//            // 示例：过滤聊天列表
//            Toast.makeText(this, "搜索功能待实现: " + query, Toast.LENGTH_SHORT).show();
//        });

//        menuButton.setOnClickListener(v -> {
//            // TODO: 实现菜单功能
//            Toast.makeText(this, "菜单功能待实现", Toast.LENGTH_SHORT).show();
//        });

        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                        provideSuggestions("");
                    }

                    @Override
                    public void onMenuClosed() {
                        mSearchView.clearSuggestions();
                    }} );

        // 监听搜索框的查询变化
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            if (!oldQuery.isEmpty() && newQuery.isEmpty()) {
                mSearchView.clearSuggestions();
            } else {
                // 提供建议项
                provideSuggestions(newQuery);
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(String currentQuery) {
                // 执行搜索操作
                saveSearchHistory(currentQuery);
                performSearch(currentQuery);
            }

            @Override
            public void onSuggestionClicked(SearchSuggestion suggestion) {
                // 处理建议项点击事件
                mSearchView.setSearchText(suggestion.getBody());
                performSearch(suggestion.getBody());
            }
        });

        // 点击聊天列表项跳转到具体聊天界面
        chatListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                Intent intent = new Intent(ChatListActivity.this, NotificationActivity.class);
                startActivity(intent);
            } else {
                // TODO: 跳转到聊天界面
                Intent intent = new Intent(ChatListActivity.this, ChatMsgView.class);
                intent.putExtra("chat_id", lastChatList.get(position).getId()); // 传递聊天 ID
                intent.putExtra("chat_name", lastChatList.get(position).getName());
                startActivity(intent);
            }
        });

        // 这里可以设置其他初始化逻辑，比如加载数据等
        Button messagesButton = findViewById(R.id.button_messages);
        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        Button userButton = findViewById(R.id.button_profile);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button squareButton = findViewById(R.id.button_square);
        squareButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, CommodityListActivity.class);
            startActivity(intent);
        });

        Button homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, HomepageActivity.class);
            startActivity(intent);
        });

        Button sellButton = findViewById(R.id.button_sell);
        sellButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, AddCommodityActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void loadChatItem(String userId) {
        lastChatList.add(new ChatItem(Long.parseLong(userId), "系统通知", "点击查看"));
        List<Contact> contacts = DBFunction.getAllContacts(userId);
        for (Contact contact: contacts) {
            lastChatList.add(new ChatItem(contact.getContactsId(), contact.getContactsName(), contact.getLastContent()));
        }
    }

    private void provideSuggestions(String query) {
        SharedPreferences preferences = getSharedPreferences("contacts_search_history", MODE_PRIVATE);
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


    @SuppressLint("MutatingSharedPrefs")
    private void saveSearchHistory(String query) {
        SharedPreferences preferences = getSharedPreferences("contacts_search_history", MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("history", new HashSet<>());
        if (!query.isEmpty()) {
            historySet.add(query);
        }
        preferences.edit().putStringSet("history", historySet).apply();
    }


    private void performSearch(String query) {
        // 实现搜索逻辑，比如过滤聊天列表
        // Toast.makeText(this, "搜索: " + query, Toast.LENGTH_SHORT).show();
        ArrayList<ChatItem> newChatList = new ArrayList<>();
        List<Contact> contacts = DBFunction.getAllContacts(userId);
        String regex = ".*" + query.replace("", ".*") + ".*";
        Pattern pattern = Pattern.compile(regex);
        for (Contact contact: contacts) {
            Matcher matcher = pattern.matcher(contact.getContactsName());
            int dist = distance.apply(query, contact.getContactsName());
            if (query.length() <= 3 ) {
                if (dist < 2 || matcher.find()) {
                    newChatList.add(new ChatItem(contact.getContactsId(), contact.getContactsName(), contact.getLastContent()));
                }
            } else if (query.length() <= 6) {
                if (dist < 4 || matcher.find()) {
                    newChatList.add(new ChatItem(contact.getContactsId(), contact.getContactsName(), contact.getLastContent()));
                }
            } else {
                if (dist < 6 || matcher.find()) {
                    newChatList.add(new ChatItem(contact.getContactsId(), contact.getContactsName(), contact.getLastContent()));
                }
            }
        }
        chatAdapter = new ChatAdapter(this, newChatList);
        chatListView.setAdapter(chatAdapter);
    }


}
