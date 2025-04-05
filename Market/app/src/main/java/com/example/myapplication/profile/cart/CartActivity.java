package com.example.myapplication.profile.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.CartItem;
import com.example.myapplication.square.BuyCommodityActivity;
import com.example.myapplication.square.CommodityDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnChangeListener{

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView totalPriceTextView;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();

        recyclerView = findViewById(R.id.recycler_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTextView = findViewById(R.id.text_view_total_price);
        buyButton = findViewById(R.id.button_buy_now);

        // 添加示例商品到购物车
//        CartItem cartItem = new CartItem("商品1", 1L, 20.00F, 2);
//        cartManager.addItemToCart(cartItem);

        // 更新 RecyclerView
        cartAdapter = new CartAdapter(cartManager.getCartItems(), this, cartManager);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.setOnChangeListener(this);

        // 显示总价
        updateTotalPrice();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        buyButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 BuyCommodityActivity
            Intent intent = new Intent(CartActivity.this, BuyCommodityActivity.class);

            // 传递商品 ID 和其他相关数据（例如价格、商品名等）
            ArrayList<Long> commodities = new ArrayList<>();
            ArrayList<Integer> quantityList = new ArrayList<>();
            List<CartItem> cartItems = cartManager.getCartItems();
            for (CartItem cartItem : cartItems) {
                if (cartItem.isSelected()) {
                    commodities.add(cartItem.getCommodityId());
                    quantityList.add(cartItem.getQuantity());
                }
            }
            if (commodities.isEmpty()) {
                Toast.makeText(getApplicationContext(), "请选择至少一件商品", Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("commodity_list", commodities);
                intent.putExtra("quantity_list", quantityList);

                // 启动 BuyCommodityActivity
                startActivity(intent);
            }
        });
    }

    private void updateTotalPrice() {
        double totalPrice = cartManager.getTotalPrice();
        totalPriceTextView.setText(String.format("总价: $%.2f", totalPrice));
    }

    @Override
    public void onChange(String message) {
        if (message.equals("change")) {
            updateTotalPrice();
        }
    }
}
