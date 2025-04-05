package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

public class Notification extends LitePalSupport {
    private long id;
    private String title;
    private long commodityId;
    private long orderId;
    private String message;
    private long recipientId; // 接收者用户ID
    private long senderId;    // 发送者用户ID
    private String type;        // 通知类型（如 "purchase", "shipped", "received"）
    private boolean isRead;     // 是否已读

    // 构造方法、getter、setter
    public Notification() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isRead() {
        return isRead;
    }
}

