package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

public class Comment extends LitePalSupport {
    private long id;
    private long commodityId;
    private String userName;
    private String title;      // 标题
    private String description;// 描述
    private String date;       // 日期
    private float star;          // 评星
    private String imageResource;

    public Comment() {

    }

    public User getUser() {
        User user = DBFunction.findUserByName(userName);
        if (user != null) {
            return user;
        } else {
            throw new NullPointerException("外键约束异常：没有找到对应的user");
        }
    }

    // 普通 get & set 方法


    public void setStar(float star) {
        this.star = star;
    }

    public float getStar() {
        return star;
    }

    public long getId() {
        return id;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

}
