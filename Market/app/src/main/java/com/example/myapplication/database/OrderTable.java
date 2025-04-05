package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

public class OrderTable extends LitePalSupport {
    private long id;
    private String commodityName;
    //Title 目前就是商品的名字
    private String commodityStatus;
    private Float commodityPrice;
    private int commodityNum;
    // private int imageResourceId;// 假设图片是资源 ID
    private String imageUrl;
    private long commodityId;
    private long userId;

//    public Order(String title, String status, double price, String imageUrl, long commodityId, long userId) {
//        this.title = title;
//        this.status = status;
//        this.price = price;
//        this.imageUrl = imageUrl;
//        this.commodityId = commodityId;
//        this.userId = userId;
//    }

    public OrderTable() {}


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCommodityNum(int commodityNum) {
        this.commodityNum = commodityNum;
    }

    public int getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityStatus(String commodityStatus) {
        this.commodityStatus = commodityStatus;
    }

    public String getCommodityStatus() {
        return commodityStatus;
    }

    public void setCommodityPrice(Float commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public Float getCommodityPrice() {
        return commodityPrice;
    }

//    public void setImageResourceId(int imageResourceId) {
//        this.imageResourceId = imageResourceId;
//    }
//
//    public int getImageResourceId() {
//        return imageResourceId;
//    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

