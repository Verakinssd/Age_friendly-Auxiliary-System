package com.example.myapplication.home;

import static com.example.myapplication.R.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.FloatViewService;
import com.example.myapplication.chat.ChatListActivity;
import com.example.myapplication.profile.ProfileActivity;
import com.example.myapplication.square.CommodityListActivity;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_homepage);

        EditText editQueryInput = findViewById(id.edit_query_input);
        Button buttonCallApi = findViewById(id.button_call_api);

        buttonCallApi.setOnClickListener(v -> {
            String query = editQueryInput.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(HomepageActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "请授权悬浮窗权限", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;
                }
            }

            Intent intent = new Intent(HomepageActivity.this, FloatViewService.class);
            intent.putExtra("query", query);
            startService(intent);
        });

        Button messagesButton = findViewById(id.button_messages);
        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        Button userButton = findViewById(id.button_profile);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button squareButton = findViewById(id.button_square);
        squareButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, CommodityListActivity.class);
            startActivity(intent);
        });

        Button homeButton = findViewById(id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
            startActivity(intent);
        });
    }
}