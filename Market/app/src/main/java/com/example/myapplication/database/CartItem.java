package com.example.myapplication.database;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

public class CartItem extends LitePalSupport {
    private long id;
    private String username;
    private String name;  // 商品名称
    private long commodityId;      // 商品id
    private Float price; // 商品价格
    private int quantity; // 商品数量
    private boolean isSelected;

    // 构造函数
    public CartItem(String name, long commodityId, Float price, int quantity) {
        this.name = name;
        this.commodityId = commodityId;
        this.price = price;
        this.quantity = quantity;
        isSelected = true;
    }

    // Getter 和 Setter 方法

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static CartItem parseCartItemFromString(String addressString) {
        // 移除 "Address{" 和 "}" 字符
        String trimmedString = addressString.replace("CartItem{", "").replace("}", "");
        // 通过逗号分割字段
        String[] parts = trimmedString.split(", ");
        long id = 0;
        String name = "";
        Float price = 0F;
        int quantity = 1;
        // 遍历每个字段并提取数据
        for (String part : parts) {
            if (part.startsWith("name='")) {
                name = part.substring(6, part.length() - 1);  // 去掉 'name=' 和结尾的 '
            } else if (part.startsWith("id='")) {
                id = Long.parseLong(part.substring(4, part.length() - 1));
            } else if (part.startsWith("price='")) {
                price = Float.parseFloat(part.substring(7, part.length() - 1));
            } else if (part.startsWith("quantity=")) {
                quantity = Integer.parseInt(part.substring(9));
            }
        }
        // 返回解析后的 Address 对象
        return new CartItem(name, id, price, quantity);
    }

    @NonNull
    @Override
    public String toString() {
        return "CartItem{" +
                "name='" + name + '\'' +
                ", id='" + commodityId + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                "}";
    }
}