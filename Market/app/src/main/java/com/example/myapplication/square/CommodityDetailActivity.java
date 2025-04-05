package com.example.myapplication.square;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.bumptech.glide.Glide;
import com.example.myapplication.InputNumberView;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Tools;
import com.example.myapplication.chat.ChatMsgView;
import com.example.myapplication.database.Comment;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Hobby;
import com.example.myapplication.database.Type;
import com.example.myapplication.database.User;
import com.example.myapplication.profile.cart.CartActivity;
import com.example.myapplication.database.CartItem;
import com.example.myapplication.profile.cart.CartManager;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommodityDetailActivity extends AppCompatActivity {
    private TextView commodityName; // 商品名称
    private TextView commodityPrice; // 商品价格
    private TextView commodityDescription; // 商品描述
    private ViewPager2 commodityImage; // 商品图片
    private TextView commoditySeller;
    private ImageButton chatButton; // 将Button改为ImageButton
    private Button editButton;
    private Commodity commodity;
    private Button saveButton;
    private Button buyButton;
    private Button addCartButton;
    private ImageButton addHobbyButton;
    private ImageButton cartButton;
    private CartManager cartManager = CartManager.getInstance();
    private InputNumberView quantity;
    private InputNumberView buyNum;
    private RecyclerView commentRecyclerView;
    private CommodityCommentAdapter commentAdapter;
    private List<Comment> commentList;
    private TextView commodityStock; // 添加库存数量显示控件
    private Button delButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);

        commodityName = findViewById(R.id.commodity_name);
        commodityPrice = findViewById(R.id.commodity_price);
        commodityDescription = findViewById(R.id.commodity_description);
        commodityImage = findViewById(R.id.commodity_image);
        commoditySeller = findViewById(R.id.commodity_seller); // 绑定卖家信息
        chatButton = findViewById(R.id.button_want); // 绑定“想要”按钮
        editButton = findViewById(R.id.button_edit_commodity); //
        addCartButton = findViewById(R.id.button_add_to_cart);
        saveButton = findViewById(R.id.button_save);
        addHobbyButton = findViewById(R.id.button_add_to_hobby);
        quantity = findViewById(R.id.commodity_num);
        buyButton = findViewById(R.id.button_buy);
        buyNum = findViewById(R.id.commodity_buy_num);
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        commodityStock = findViewById(R.id.commodity_stock);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        delButton = findViewById(R.id.button_del_commodity);

        // 重定向到自己的购物车
        cartButton = findViewById(R.id.button_cart);
        cartButton.setOnClickListener(v-> {
            Intent intent = new Intent(CommodityDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        //从 Intent 中获取商品 ID
        Intent intent = getIntent();
        long commodityId = intent.getLongExtra("commodity_id", -1);
        commodity = LitePal.find(Commodity.class, commodityId);

        // 确保 ID 有效并执行后续逻辑
        if (commodityId != -1) {
            loadCommodityDetails(commodityId); // 根据 ID 加载商品详情
        } else {
            Toast.makeText(this, "商品 ID 无效", Toast.LENGTH_SHORT).show();
        }

        //检测当前是否是卖家，是就显示修改和下架按钮,不显示聊天按钮
        if (isCurrentUserSeller(commodity)) {
            editButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.GONE);
            addCartButton.setVisibility(View.GONE);
            buyButton.setVisibility(View.GONE);
            quantity.setVisibility(View.GONE);
            buyNum.setVisibility(View.GONE);
            editButton.setOnClickListener(v -> enableEditing());
            delButton.setVisibility(View.VISIBLE);
            delButton.setOnClickListener(view -> delCommodity());
        }
        //保存按钮
        saveButton.setOnClickListener(v -> saveChanges());

        // 返回按钮
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        loadComments(); //添加评论
    }

    protected void onResume() {
        super.onResume();
        // 在这里执行你希望在Activity重新回到前台后执行的逻辑
        Intent intent = getIntent();
        long commodityId = intent.getLongExtra("commodity_id", -1);
        loadCommodityDetails(commodityId);
    }

    @SuppressLint("SetTextI18n")
    private void loadCommodityDetails(long commodityId) {
        // 使用商品 ID 获取商品详情
        Commodity commodity = LitePal.find(Commodity.class, commodityId);
        // 确保商品不为空后设置视图
        if (commodity != null) {
            commodityName.setText(commodity.getCommodityName());
            commodityPrice.setText("¥ " + commodity.getPrice());
            commodityDescription.setText(commodity.getDescription());
            commoditySeller.setText("卖家: " + commodity.getSellerName()); // 显示卖家信息

            // 添加库存数量显示
            commodityStock.setText("库存数量: " + commodity.getNumber());

            // 限制购买数量不能超过库存
            buyNum.setMaxNum(commodity.getNumber());
            buyNum.setOnAmountChangeListener((view, amount) -> {
                if (amount > commodity.getNumber()) {
                    Toast.makeText(getApplicationContext(), "超过库存数量", Toast.LENGTH_SHORT).show();
                    buyNum.setCurrentNum(commodity.getNumber());
                }
            });

            // 限制加入购物车数量不能超过库存
            quantity.setMaxNum(commodity.getNumber());
            quantity.setOnAmountChangeListener((view, amount) -> {
                if (amount > commodity.getNumber()) {
                    Toast.makeText(getApplicationContext(), "超过库存数量", Toast.LENGTH_SHORT).show();
                    quantity.setCurrentNum(commodity.getNumber());
                }
            });

            // 加载商品图片
            String imageBase64 = commodity.getImageUrl();
            List<String> pictures = commodity.getPictures();
            if (pictures.isEmpty() && (imageBase64 != null && !imageBase64.isEmpty())) {
                pictures.add(imageBase64);
                commodity.addPicture(imageBase64);
                commodity.save();
            }
            List<String> base64ImageList = new ArrayList<>(pictures);
            MyImageSliderAdapter adapter = new MyImageSliderAdapter(base64ImageList);
            commodityImage.setAdapter(adapter);


            chatButton.setOnClickListener(v -> {
                long user2 = DBFunction.findUserByName(commodity.getSellerName()).getId();
                Intent intent = new Intent(CommodityDetailActivity.this, ChatMsgView.class);
                intent.putExtra("commodity_id", commodityId);
                intent.putExtra("chat_id", user2); // 传递聊天 ID
                intent.putExtra("chat_name", commodity.getSellerName());
                startActivity(intent);
            });

//            // 加入商品数量
//            quantity.setMaxNum(50);
//            quantity.setOnAmountChangeListener(new InputNumberView.OnAmountChangeListener() {
//                @Override
//                public void onAmountChange(View view, int amount) {
//                    if (amount > 50) {
//                        Toast.makeText(getApplicationContext(), "未找到该商品", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

            // 已添加
            TextView already = findViewById(R.id.commodity_already_num);
            already.setText("已添加：" + cartManager.getQuantity(commodity.getId()));

            // 加入购物车
            addCartButton.setOnClickListener(v -> {
                CartItem cartItem = new CartItem(commodity.getCommodityName()
                        , commodity.getId()
                        , commodity.getPrice()
                        , quantity.getCurrentNum());
                cartManager.addItemToCart(cartItem);
                Tools.toastMessageShort(CommodityDetailActivity.this, "加入购物车成功!");
                already.setText("已添加：" + cartManager.getQuantity(cartItem));
            });



            addHobbyButton.setOnClickListener(v -> {
                Hobby hobby = new Hobby();
                hobby.setCommodityId(commodity.getId());
                hobby.setTitle(commodity.getCommodityName());
                hobby.setId(commodity.getId());
                hobby.setUserName(MainActivity.getCurrentUsername());
                hobby.save();
                User user1 = DBFunction.findUserByName(MainActivity.getCurrentUsername());
                user1.addHobby(hobby);
                user1.save();
                String s = "no";
                if (!user1.getHobbies().isEmpty()) {
                    s = "yes";
                    //Tools.toastMessageShort(CommodityDetailActivity.this, DBFunction.findUserByName(MainActivity.getCurrentUsername()).getHobbies().get(0).getTitle());
                }
                Tools.toastMessageShort(CommodityDetailActivity.this, s);
            });

//            buyNum.setMaxNum(50);
//            buyNum.setOnAmountChangeListener(new InputNumberView.OnAmountChangeListener() {
//                @Override
//                public void onAmountChange(View view, int amount) {
//                    if (amount > 50) {
//                        Toast.makeText(getApplicationContext(), "超过最大购买上限", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

            buyButton.setOnClickListener(v -> {
                // 创建 Intent 跳转到 BuyCommodityActivity
                Intent intent = new Intent(CommodityDetailActivity.this, BuyCommodityActivity.class);
                // 传递商品 ID 和其他相关数据（例如价格、商品名等）
                ArrayList<Long> commodities = new ArrayList<>();
                ArrayList<Integer> quantityList = new ArrayList<>();
                commodities.add(commodity.getId());
                quantityList.add(buyNum.getCurrentNum());
                intent.putExtra("commodity_list", commodities);
                intent.putExtra("quantity_list", quantityList);
                intent.putExtra("commodity_id", commodity.getId());
                intent.putExtra("commodity_name", commodity.getCommodityName());
                intent.putExtra("commodity_price", commodity.getPrice());
                // intent.putExtra("commodity_image_url", commodity.getImageUrl());

                // 启动 BuyCommodityActivity
                startActivity(intent);
            });

        } else {
            Toast.makeText(this, "未找到该商品", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCurrentUserSeller(Commodity commodity) { // 检查当前用户是否为卖家
        return Objects.equals(commodity.getSellerName(), MainActivity.getCurrentUsername()); // 替换为实际逻辑
    }

    private void enableEditing() { // 只有卖家能编辑
        if (isCurrentUserSeller(commodity)) {
            commodityPrice.setEnabled(true);
            commodityDescription.setEnabled(true);
            commodityStock.setEnabled(true);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "无法编辑商品信息", Toast.LENGTH_SHORT).show();
        }
    }

    private void delCommodity() {
        if (isCurrentUserSeller(commodity)) {
            commodity.setNumber(0);
            commodity.save();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "无法下架商品", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String description = commodityDescription.getText().toString();
        String priceString = commodityPrice.getText().toString();
        String stockString = commodityStock.getText().toString();
        float price;
        // 检查价格输入是否为空
        if (priceString.isEmpty()) {
            Toast.makeText(this, "价格不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 移除非数字和小数点字符
        priceString = priceString.replaceAll("[^\\d.]", "");
        try {
            price = Float.parseFloat(priceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show();
            return;
        }

        int num;
        stockString = stockString.replaceAll("[^\\d.]", "");
        try {
            num = Integer.parseInt(stockString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数量", Toast.LENGTH_SHORT).show();
            return;
        }
        // 更新商品对象
        commodity.setDescription(description);
        commodity.setPrice(price);
        commodity.setNumber(num);
        commodity.save(); // 保存更新到数据库

        Toast.makeText(this, "商品信息已更新", Toast.LENGTH_SHORT).show();

        // 禁用编辑模式
        disableEditing();

        //关闭当前页面
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated", true); // 或其他需要返回的数据
        setResult(RESULT_OK, resultIntent);
        finish(); // 关闭当前活动
    }

    private void disableEditing() {
        commodityPrice.setEnabled(false);
        commodityDescription.setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }

    private void loadComments() {
        try {
            commentList = DBFunction.findCommentsByCommodityId(commodity.getId());
            if (commentList != null && !commentList.isEmpty()) {
                commentAdapter = new CommodityCommentAdapter(this, commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            } else {
                Toast.makeText(this, "没有评论", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载评论失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
