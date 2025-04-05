package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

public class Message extends LitePalSupport {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private long id; // 消息唯一标识
    private long senderId; // 发送者 ID
    private long receiverId; // 接收者 ID
    private String receiverName;
    private String content; // 消息内容
    private long timestamp; // 发送时间戳
    private boolean isRead; // 是否已读

    public Message() {
    }

    public long getId() {
        return id;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getContent() {
        return content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
