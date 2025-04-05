package com.example.myapplication.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class DeveloperNewsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_news);

        Button button = findViewById(R.id.agree_button);

        button.setOnClickListener(v -> {
            finish();
        });
    }
}
