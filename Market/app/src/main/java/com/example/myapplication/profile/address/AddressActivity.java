package com.example.myapplication.profile.address;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        recyclerView = findViewById(R.id.recycler_view_address);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 从数据库加载地址数据
        addressList = initAddressList();

        // 设置适配器
        addressAdapter = new AddressAdapter(addressList, this);
        recyclerView.setAdapter(addressAdapter);

        // 添加新地址按钮
        Button addAddressButton = findViewById(R.id.button_add_address);
        addAddressButton.setOnClickListener(v -> {
            // TODO: 打开添加地址的Activity或者弹出一个Dialog
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivityForResult(intent, 1);
        });

        // 返回按钮
        ImageButton returnButton = findViewById(R.id.back_button);
        returnButton.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String newAddress = data.getStringExtra("new_address");

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // TODO: 从数据库加载最新的地址列表并更新 RecyclerView
            if (newAddress != null) {
                // 添加新的地址到列表并刷新 RecyclerView
                //addressList.add(Address.parseAddressFromString(newAddress));
                addressList.clear();
                addressList.addAll(initAddressList());
                addressAdapter.notifyItemInserted(addressList.size() - 1);  // 刷新新增项
                addressAdapter.notifyItemRangeChanged(0, addressList.size());
                recyclerView.setAdapter(addressAdapter);
            }
        }
    }


    public ArrayList<Address> initAddressList() {
        ArrayList<String> addressString = DBFunction.getAddress(MainActivity.getCurrentUsername());
        ArrayList<Address> addresses = new ArrayList<>();
        for (String address: addressString) {
            addresses.add(Address.parseAddressFromString(address));
        }
        return addresses;
    }
}
