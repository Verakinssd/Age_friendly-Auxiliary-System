package com.example.myapplication.profile;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DBFunction;
import com.example.myapplication.home.HomepageActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView userMoney;
    private TextView userSignature;

    // 假设 currentUser 是当前登录的用户名
    private final String currentUser = MainActivity.getCurrentUsername(); // 这个可以根据实际情况进行获取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);

        usernameTextView.setText(currentUser);

        userSignature = findViewById(R.id.user_signature);
        String signature = DBFunction.findUserByName(MainActivity.getCurrentUsername()).getPersonalitySign();
        if (signature != null) {
            userSignature.setText(signature);
        } else {
            userSignature.setText("^_^");
        }

        String userImageUrl = MainActivity.getCurrentImageUrl();

        if (userImageUrl != null && !userImageUrl.isEmpty()) {
            byte[] decodedString = Base64.decode(userImageUrl, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0 , decodedString.length);
            profileImageView.setImageBitmap(decodedByte);
        }

        RelativeLayout settingsButton = findViewById(R.id.my_settings_view);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button userButton = findViewById(R.id.button_profile);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
            startActivity(intent);
        });
    }
}
