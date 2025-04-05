package com.example.myapplication.profile;

public class FavoriteItem {

    private int imageResource; // 图片资源ID
    private String title;      // 收藏标题
    private String description;// 收藏描述
    private String date;       // 收藏日期

    public FavoriteItem(int imageResource, String title, String description, String date) {
        this.imageResource = imageResource;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}

