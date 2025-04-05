package com.example.myapplication.profile;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Hobby;
import com.example.myapplication.profile.FavoriteItem;
import com.example.myapplication.profile.FavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<Hobby> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite); // 绑定布局文件

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recycler_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 设置布局管理器为线性布局

        // 模拟数据（实际项目中，从数据库或API获取数据） TODO
        favoriteList = DBFunction.findHobbyByName(MainActivity.getCurrentUsername());
        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
        }
        // 初始化适配器并绑定数据
        favoriteAdapter = new FavoriteAdapter(this, favoriteList);
        recyclerView.setAdapter(favoriteAdapter); // 设置适配器

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    public void update() {
        favoriteList = DBFunction.findUserByName(MainActivity.getCurrentUsername()).getHobbies();
    }
}
