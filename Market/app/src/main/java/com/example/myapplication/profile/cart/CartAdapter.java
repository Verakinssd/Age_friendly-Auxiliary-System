package com.example.myapplication.profile.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.InputNumberView;
import com.example.myapplication.R;
import com.example.myapplication.database.CartItem;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.square.CommodityDetailActivity;

import org.litepal.LitePal;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartManager cartManager;
    private OnChangeListener mListener;

    public CartAdapter(List<CartItem> cartItems, Context context, CartManager cartManager) {
        this.cartItems = cartItems;
        this.context = context;
        this.cartManager = cartManager;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.nameTextView.setText(cartItem.getName());
        holder.priceTextView.setText(String.format("$%.2f", cartItem.getPrice()));
        holder.inputNumber.setMaxNum(50);
        holder.checkBox.setChecked(cartItem.isSelected());

        Commodity commodity = LitePal.find(Commodity.class, cartItem.getCommodityId());
        String imageBase64 = commodity.getImageUrl();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.commodityImg.setImageBitmap(decodedByte);
        }

        holder.inputNumber.setOnAmountChangeListener(new InputNumberView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, int amount) {
                cartItem.setQuantity(amount);
                cartItem.save();
                if (mListener != null) {
                    mListener.onChange("change");
                }
            }
        });
        holder.inputNumber.setCurrentNum(cartItem.getQuantity());

        // 移除商品
        holder.removeButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("确认删除")
                    .setMessage("您确定要从购物车删除这个商品吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        cartManager.removeItemFromCart(position);
                        // cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                    }).setNegativeButton("取消", (dialog, which) -> {
                        // 如果用户取消，什么都不做
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        // 设置选择框
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            cartItem.save();
            if (mListener != null) {
                mListener.onChange("change");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            // 根据你的需要实现点击后的逻辑，比如跳转到详情页
            Log.d("CommodityAdapter", "点击的商品 ID: " + cartItem.getCommodityId());
            Intent intent = new Intent(context, CommodityDetailActivity.class);
            intent.putExtra("commodity_id", cartItem.getCommodityId());
            //context.startActivity(intent);
            ((Activity) context).startActivityForResult(intent, 1); // 使用传递的请求码
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        ImageView commodityImg;
        // TextView quantityTextView;
        ImageButton removeButton;
        InputNumberView inputNumber;
        CheckBox checkBox;

        public CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.commodity_name);
            priceTextView = itemView.findViewById(R.id.commodity_price);
            commodityImg = itemView.findViewById(R.id.commodity_image);
            // quantityTextView = itemView.findViewById(R.id.commodity_num);
            removeButton = itemView.findViewById(R.id.cart_item_remove_button);
            inputNumber = itemView.findViewById(R.id.commodity_num);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public interface OnChangeListener {
        void onChange(String message);
    }
}

