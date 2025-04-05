package com.example.myapplication.profile.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R; // 确保导入正确的包
import com.example.myapplication.database.DBFunction;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private List<Address> addressList;
    private Context context;

    public AddressAdapter(List<Address> addressList, Context context) {
        this.addressList = addressList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.addressName.setText(address.getName());
        holder.addressDetail.setText(address.getDetail());
        holder.phone.setText(address.getPhoneNumber());
        holder.isDefault.setText(address.isDefault() ? "默认地址" : "");

        // 删除按钮点击事件 TODO
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("确认删除")
                    .setMessage("您确定要删除这个地址吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        addressList.clear();
                        DBFunction.delAddress(MainActivity.getCurrentUsername(), position);
                        addressList.addAll(updateAddressList());
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0, addressList.size());
//                        notifyItemChanged(0);
                    }).setNegativeButton("取消", (dialog, which) -> {
                        // 如果用户取消，什么都不做
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressName, addressDetail, phone, isDefault;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressName = itemView.findViewById(R.id.address_name);
            addressDetail = itemView.findViewById(R.id.address_detail);
            phone = itemView.findViewById(R.id.address_phone);
            deleteButton = itemView.findViewById(R.id.button_delete_address);
            isDefault = itemView.findViewById(R.id.address_default);
        }
    }

    public ArrayList<Address> updateAddressList() {
        ArrayList<String> addressString = DBFunction.getAddress(MainActivity.getCurrentUsername());
        ArrayList<Address> addresses = new ArrayList<>();
        for (String address: addressString) {
            addresses.add(Address.parseAddressFromString(address));
        }
        return addresses;
    }
}

