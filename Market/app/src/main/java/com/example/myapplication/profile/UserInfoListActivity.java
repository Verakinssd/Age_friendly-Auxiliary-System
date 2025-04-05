package com.example.myapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class UserInfoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_list);

        Button agreeButton = findViewById(R.id.agree_button);
        Button exitButton = findViewById(R.id.exit_button);

        agreeButton.setOnClickListener(v -> {
            // 处理同意按钮点击事件
            finish();
        });

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
} 