package com.example.myapplication.profile.cart;

import com.example.myapplication.MainActivity;
import com.example.myapplication.database.CartItem;
import com.example.myapplication.database.DBFunction;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private List<CartItem> cartItems;
    private static CartManager cartManager = new CartManager(); // 单例模式

    public static CartManager getInstance() {
        return cartManager;
    }

    private CartManager() {
        cartItems = new ArrayList<>();
        List<CartItem> carts = DBFunction.getCart(MainActivity.getCurrentUsername());
        cartItems.addAll(carts);
    }

    // 添加商品到购物车
    public void addItemToCart(CartItem item) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getCommodityId() == item.getCommodityId()) {
                int q = cartItems.get(i).getQuantity();
                cartItems.get(i).setQuantity(q + item.getQuantity());
                cartItems.get(i).save();
                //cartItems.get(i).setQuantity(q + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
        DBFunction.addCart(MainActivity.getCurrentUsername(), item);
    }

    // 从购物车中移除商品
    public void removeItemFromCart(int postion) {
        CartItem cartItem = cartItems.remove(postion);
        DBFunction.delCart(MainActivity.getCurrentUsername(), cartItem.getCommodityId());
    }

    // 获取购物车中所有商品
    public List<CartItem> getCartItems() {
        cartItems.clear();
        List<CartItem> carts = DBFunction.getCart(MainActivity.getCurrentUsername());
        cartItems.addAll(carts);
        return cartItems;
    }

    // 获取某一商品数量
    public int getQuantity(CartItem item) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getCommodityId() == item.getCommodityId()) {
                return cartItems.get(i).getQuantity();
            }
        }
        return 0;
    }

    public int getQuantity(long commodityId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getCommodityId() == commodityId) {
                return cartItems.get(i).getQuantity();
            }
        }
        return 0;
    }

    // 计算购物车总价
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {  // 只计算选中的商品
                total += item.getPrice() * item.getQuantity();
            }
        }
        return total;
    }
}

