package com.example.myapplication.notification;

import static org.litepal.LitePalApplication.getContext;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Notification;
import com.example.myapplication.database.OrderTable;

import org.litepal.LitePal;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notificationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onActionClick(Notification notification, int position);
    }

    public NotificationAdapter(List<Notification> notifications, OnItemClickListener listener) {
        this.notificationList = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.buyer.setText("收件人：" + DBFunction.findUserNameById(notification.getSenderId()));


        if (notification.getMessage().equals("已发货")) {
            holder.actionButton.setVisibility(View.GONE);
        }
        OrderTable orderTable = LitePal.find(OrderTable.class, notification.getOrderId());
        if (orderTable == null) {
            notification.setMessage("已取消");
            notification.save();
            holder.actionButton.setText("删除记录");
            holder.message.setText(notification.getMessage());
        }

        if (orderTable != null) {
            if (orderTable.getCommodityNum() == 0) {
                orderTable.setCommodityNum(1);
                orderTable.save();
            }
            holder.quantity.setText("x" + orderTable.getCommodityNum());
        }

        holder.actionButton.setOnClickListener(v -> {
            if (notification.getMessage().equals("待发货")) {
                DBFunction.changeCommodityStatus(notification.getId(), "已发货");
                DBFunction.changeOrderStatus(notification.getOrderId(), "已发货");
                notificationList.remove(position);
                notifyItemChanged(position);
                notifyItemRangeChanged(position, notificationList.size());
                // Toast.makeText(getContext(), "确认发货", Toast.LENGTH_SHORT).show();
                // listener.onActionClick(notification, position);
            } else if (notification.getMessage().equals("已取消")) {
                DBFunction.delNotification(notification.getId());
                notificationList.remove(position);
                notifyItemChanged(position);
                notifyItemRangeChanged(position, notificationList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void removeNotification(int position) {
        notificationList.remove(position);
    }

    public void addNotification(Notification notification) {
        notificationList.add(notification);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, buyer, quantity;
        Button actionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            buyer = itemView.findViewById(R.id.notification_buyer);
            actionButton = itemView.findViewById(R.id.notification_action_button);
            quantity = itemView.findViewById(R.id.notification_quantity);
        }
    }
}

