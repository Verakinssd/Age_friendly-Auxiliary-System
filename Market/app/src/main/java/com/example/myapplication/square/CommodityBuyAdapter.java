package com.example.myapplication.square;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.profile.address.Address;

import java.util.ArrayList;
import java.util.List;

public class CommodityBuyAdapter extends RecyclerView.Adapter<CommodityBuyAdapter.ViewHolder> {
    private List<Commodity> commodityList;
    private List<Integer> quantityList;
    private List<String> chosenAddresses;
    private ArrayList<String> addressList; // 地址列表

    public CommodityBuyAdapter(List<Commodity> commodityList, List<Integer> quantityList, List<String> chosenAddresses) {
        this.commodityList = commodityList;
        this.quantityList = quantityList;
        this.chosenAddresses = chosenAddresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commodity_buy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Commodity commodity = commodityList.get(position);
        holder.commodityName.setText(commodity.getCommodityName());
        holder.commodityPrice.setText(String.format("¥%.2f", commodity.getPrice()));
        holder.quantityText.setText(String.valueOf(quantityList.get(holder.getAdapterPosition())));
        // 地址选择

        // 初始化地址列表
        addressList = DBFunction.getAddress(MainActivity.getCurrentUsername());
        if (addressList.isEmpty()) {
            addressList.add("请添加地址");
        }

        ArrayList<String> strings = new ArrayList<>();
        for (String address : addressList) {
            Address address1 = Address.parseAddressFromString(address);
            strings.add(address1.getName() + " " + address1.getDetail() + " " + address1.getPhoneNumber());
        }

        // 设置地址选择器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.addressSpinner.setAdapter(adapter);
        holder.addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenAddresses.set(holder.getAdapterPosition(), addressList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosenAddresses.set(holder.getAdapterPosition(), null);
            }
        });

        // 数量选择
        holder.incrementButton.setOnClickListener(v -> {
            quantityList.set(holder.getAdapterPosition(), quantityList.get(holder.getAdapterPosition()) + 1);
            holder.quantityText.setText(String.valueOf(quantityList.get(holder.getAdapterPosition())));
        });

        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = quantityList.get(holder.getAdapterPosition());
            if (currentQuantity > 1) {
                quantityList.set(holder.getAdapterPosition(), currentQuantity - 1);
                holder.quantityText.setText(String.valueOf(quantityList.get(holder.getAdapterPosition())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return commodityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commodityName, commodityPrice, quantityText;
        Button incrementButton, decrementButton;
        Spinner addressSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commodityName = itemView.findViewById(R.id.commodity_name);
            commodityPrice = itemView.findViewById(R.id.commodity_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
            addressSpinner = itemView.findViewById(R.id.address_spinner);
        }
    }

}
