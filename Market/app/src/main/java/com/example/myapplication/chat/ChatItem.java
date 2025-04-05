package com.example.myapplication.chat;

public class ChatItem {
    private long id; // 聊天 ID
    private String name; // 聊天者名字
    private String lastMessage; // 最后消息

    public ChatItem(long id, String name, String lastMessage) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}

