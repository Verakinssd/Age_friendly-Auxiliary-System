package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;


public class Commodity extends LitePalSupport {
    private long id; //编号
    private String commodityName; //名称
    // TODO 添加商品图片
    private String imageUrl;
    private ArrayList<String> pictures = new ArrayList<>();
    private Float price; //价格
    private String releaseDate; //发布日期
    private String description; //商品描述
    private int typeValue; //商品类别
    private String sellerName;
    private String buyerName;
    private int number;//商品数量

    public Commodity() {
    }

    // set && get
    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPrice() {
        return price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public Type getType() {
        return Type.getTypeByValue(typeValue);
    }

    public ArrayList<String> getPictures() {
        if (pictures == null) {
            pictures = new ArrayList<>();
            this.save();
        }
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public void addPicture(String picture) {
        pictures.add(picture);
    }
}
