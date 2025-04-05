package com.example.myapplication.square;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.OrderTable;
import com.example.myapplication.database.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BuyCommodityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button buyAllButton;
    private TextView exitButton;

    private List<Commodity> commodityList = new ArrayList<>();
    private List<Integer> quantityList;
    private List<String> chosenAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_commodity);

        recyclerView = findViewById(R.id.recycler_view);
        buyAllButton = findViewById(R.id.buy_all_button);
        exitButton = findViewById(R.id.exit_text);

        // 获取传递的商品列表
        Intent intent = getIntent();
        List<Long> commodityIdList = new ArrayList<>();
        commodityIdList = (List<Long>) intent.getSerializableExtra("commodity_list");
        for (Long commodityId : commodityIdList) {
            Commodity commodity = LitePal.find(Commodity.class, commodityId);
            commodityList.add(commodity);
        }


        if (commodityList == null || commodityList.isEmpty()) {
            Toast.makeText(this, "商品列表加载失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化数据
        quantityList = (List<Integer>) intent.getSerializableExtra("quantity_list");
        chosenAddresses = new ArrayList<>();
        for (int i = 0; i < commodityList.size(); i++) {
            chosenAddresses.add(null); // 默认地址
        }

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommodityBuyAdapter adapter = new CommodityBuyAdapter(commodityList, quantityList, chosenAddresses);
        recyclerView.setAdapter(adapter);

        // 确认购买
        buyAllButton.setOnClickListener(v -> {
            for (int i = 0; i < commodityList.size(); i++) {
                Commodity commodity = commodityList.get(i);
                int quantity = quantityList.get(i);
                String chosenAddress = chosenAddresses.get(i);

                if (chosenAddress == null || chosenAddress.equals("请添加地址")) {
                    Toast.makeText(this, "请选择有效的收货地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                User buyer = DBFunction.findUserByName(MainActivity.getCurrentUsername());
                User seller = DBFunction.findUserByName(commodity.getSellerName());
                if (buyer.getMoney() >= commodity.getPrice() * quantity) {
                    if (commodity.getNumber() >= quantity) {
                        buyer.buy(commodity.getPrice() * quantity);
                        buyer.save();
                        commodity.setNumber(commodity.getNumber() - quantity);
                        commodity.save();
                        OrderTable order = DBFunction.addBuyOrder(commodity.getId(), buyer.getId(), commodity.getCommodityName(),
                                commodity.getPrice(), commodity.getImageUrl(), quantity);
                        DBFunction.sendNotification2Seller(commodity.getCommodityName(), order.getId(), seller.getId(), buyer.getId(), "待发货");
                    } else {
                        Toast.makeText(this, commodity.getCommodityName() + " 商品余量不足", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "购买 "+ commodity.getCommodityName()+" * " + quantity+ " 余额不足", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "购买成功！", Toast.LENGTH_SHORT).show();
            finish();
        });

        exitButton.setOnClickListener(v -> {
            finish();
        });

    }
}
