package com.example.myapplication.profile;

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
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.User;
import com.example.myapplication.profile.comment.AddCommentActivity;
import com.example.myapplication.database.OrderTable;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderTable> orderList;

    public OrderAdapter(Context context, List<OrderTable> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderTable order = orderList.get(position);

        // 绑定订单标题
        holder.orderTitle.setText(order.getCommodityName());

        // 绑定订单状态
        holder.orderStatus.setText(order.getCommodityStatus());

        // 绑定订单价格
        holder.orderPrice.setText(String.format("¥%.2f", order.getCommodityPrice()));

        // 显示订单数量
        if (order.getCommodityNum() == 0) {
            order.setCommodityNum(1);
            order.save();
        }
        holder.orderQuantity.setText("x" + order.getCommodityNum());

        // 绑定订单图片
        // holder.orderImage.setImageResource(order.getImageResourceId());
        String imageBase64 = order.getImageUrl();
        if (imageBase64 != null || !imageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.orderImage.setImageBitmap(decodedByte);
        }

        // 动态设置状态颜色（如“待发货”是黄色，“已完成”是绿色）
        switch (order.getCommodityStatus()) {
            case "待发货":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "已完成":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "已取消":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        //“已完成”才显示“评论”按钮
        if ("已完成".equals(order.getCommodityStatus())) {
            holder.addComment.setVisibility(View.VISIBLE);
        } else {
            holder.addComment.setVisibility(View.GONE);
        }
        holder.addComment.setOnClickListener(v -> {
            // 根据你的需要实现点击后的逻辑，比如跳转到详情页
            Log.d("CommodityAdapter", "点击的商品名字: " + order.getCommodityName());
            Intent intent = new Intent(context, AddCommentActivity.class);
            intent.putExtra("commodity_id", order.getCommodityId());
            //context.startActivity(intent);
            ((Activity) context).startActivityForResult(intent, 1); // 使用传递的请求码
        });

//        holder.delOrder.setEnabled(order.getCommodityStatus().equals("待发货"));

        holder.delOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("确认删除")
                    .setMessage("您确定要删除这个订单吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        User user = DBFunction.findUserByName(MainActivity.getCurrentUsername());
                        assert user != null;
                        user.sell(order.getCommodityPrice() * order.getCommodityNum());
                        user.save();
                        DBFunction.delOrder(order.getId());
                        orderList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0, orderList.size());
//                        notifyItemChanged(0);
                    }).setNegativeButton("取消", (dialog, which) -> {
                        // 如果用户取消，什么都不做
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        //已发货才显示 ”确认收货“ 按钮
        if (order.getCommodityStatus().equals("已发货")) {
            holder.confirmReceipt.setVisibility(View.VISIBLE);
        } else {
            holder.confirmReceipt.setVisibility(View.GONE);
        }
        holder.confirmReceipt.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("确认收货")
                    .setMessage("您确定已收到此商品吗？")
                    .setPositiveButton("确认", (dialog, which) -> {
                        // 在这里处理确认收货的逻辑，例如更新订单状态
                        order.setCommodityStatus("已完成");
                        order.save();
                        Commodity commodity = DBFunction.getCommodity(order.getCommodityId());
                        User user = DBFunction.findUserByName(commodity.getSellerName());
                        assert user != null;
                        user.sell(order.getCommodityPrice() * order.getCommodityNum());
                        user.save();
                        notifyItemChanged(position); // 更新UI
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView orderImage;
        TextView orderTitle, orderStatus, orderPrice, orderQuantity;
        Button addComment, delOrder, confirmReceipt;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderImage = itemView.findViewById(R.id.order_image);
            orderTitle = itemView.findViewById(R.id.order_title);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderQuantity = itemView.findViewById(R.id.order_quantity);
            addComment = itemView.findViewById(R.id.add_comment);
            delOrder = itemView.findViewById(R.id.del_order);
            confirmReceipt = itemView.findViewById(R.id.confirm_receipt);
        }
    }
}
