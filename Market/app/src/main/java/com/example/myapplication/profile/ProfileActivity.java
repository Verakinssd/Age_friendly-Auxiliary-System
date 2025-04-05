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

import com.example.myapplication.commodity.AddCommodityActivity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.home.HomepageActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.chat.ChatListActivity;
import com.example.myapplication.profile.address.AddressActivity;
import com.example.myapplication.profile.cart.CartActivity;
import com.example.myapplication.profile.comment.CommentActivity;
import com.example.myapplication.square.CommodityListActivity;

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

        //初始化控件
        profileImageView = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);

        // 设置用户名
        usernameTextView.setText(currentUser);
        userMoney = findViewById(R.id.user_money);
        userMoney.setText("账户余额： " + DBFunction.findUserByName(MainActivity.getCurrentUsername()).getMoney());

        userSignature = findViewById(R.id.user_signature);
        String signature = DBFunction.findUserByName(MainActivity.getCurrentUsername()).getPersonalitySign();
        if (signature != null) {
            userSignature.setText(signature);
        } else {
            userSignature.setText("^_^");
        }

        // 根据用户名设置头像
        String userImageUrl = MainActivity.getCurrentImageUrl();

        if (userImageUrl != null && !userImageUrl.isEmpty()) {
            byte[] decodedString = Base64.decode(userImageUrl, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0 , decodedString.length);
            profileImageView.setImageBitmap(decodedByte);
        }
        //profileImageView.setImageResource(avatarResId);

        ImageButton cartButton = findViewById(R.id.button_cart);
        cartButton.setOnClickListener(v-> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            startActivity(intent);
        });

        RelativeLayout orderButton = findViewById(R.id.my_order_view);
        orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        RelativeLayout commentButton = findViewById(R.id.my_comment_view);
        commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
            startActivity(intent);
        });

        RelativeLayout favoriteButton = findViewById(R.id.my_favorite_view);
        favoriteButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        RelativeLayout addressButton = findViewById(R.id.my_address_view);
        addressButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        RelativeLayout settingsButton = findViewById(R.id.my_settings_view);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


        // 这里可以设置其他初始化逻辑，比如加载数据等
        Button messagesButton = findViewById(R.id.button_messages);
        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        Button userButton = findViewById(R.id.button_profile);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button squareButton = findViewById(R.id.button_square);
        squareButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CommodityListActivity.class);
            startActivity(intent);
        });

        Button sellButton= findViewById(R.id.button_sell);
        sellButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddCommodityActivity.class);
            startActivity(intent);
        });

        Button homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
            startActivity(intent);
        });
    }

    protected void onResume() {

        super.onResume();
        userMoney.setText("账户余额： " + DBFunction.findUserByName(MainActivity.getCurrentUsername()).getMoney());
    }
}
