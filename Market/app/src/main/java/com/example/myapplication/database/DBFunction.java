package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.profile.address.Address;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DBFunction {
    public static final String TAG = "from database";

    public static User findUserByName(String username) {
        List<User> users = LitePal.where("username = ?", username).find(User.class);
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public static String findUserNameById(long id) {
        User user = LitePal.find(User.class, id);
        return user.getUsername();
    }

    public static List<Hobby> findHobbyByName(String username) {
        List<Hobby> hobbies = LitePal.where("username = ?", username).find(Hobby.class);
        if (!hobbies.isEmpty()) {
            return hobbies;
        } else {
            return null;
        }
    }

    public static void addUser(String username, String password, String registerDate,String url) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRegisterTime(registerDate);
        user.setImageUrl(url);
        user.save();
    }

    //是否允许登录
    public static boolean isAllowLogin(String username, String password) {
        return LitePal.isExist(User.class, "username = ? and password = ?", username, password);
    }

    //保证用户名唯一
    public static boolean isUsernameExist(String username) {
        return LitePal.isExist(User.class, "username = ?", username);
    }

    //修改密码
    public static void changePassword(String username, String newPassword) {
        User updateUser = new User();
        updateUser.setPassword(newPassword);
        updateUser.updateAll("username = ?", username);
    }

    public static void addCommodity(String name, String sellerName, String releaseDate,
                                    Type type, Float price, String description, String url, int number, ArrayList<String> pictures) {
        Commodity commodity = new Commodity();
        commodity.setTypeValue(type.getValue());
        commodity.setReleaseDate(releaseDate);
        commodity.setCommodityName(name);
        commodity.setPrice(price);
        commodity.setDescription(description);
        commodity.setSellerName(sellerName);
        commodity.setImageUrl(url);
        commodity.setNumber(number);
        commodity.setPictures(pictures);
        commodity.save();
    }

    public static Commodity getCommodity(long commodityId) {
        return LitePal.find(Commodity.class, commodityId);
    }

    public static List<Commodity> findCommodityNotEmpty() {
        return LitePal.where("number > ?", "0").find(Commodity.class);
    }

    // 购物车
    public static List<CartItem> getCart(String userName) {
        User user = findUserByName(userName);
        if (user != null) {
            // 查找该用户的购物车信息
            return LitePal.where("username = ?", userName).find(CartItem.class);
        } else {
            Log.w(DBFunction.TAG, "不存在用户名为 " + userName + " 的用户");
        }
        return new ArrayList<>();
    }

    public static void addCart(String userName, CartItem cartItem) {
        User user = findUserByName(userName);
        if (user != null) {
            cartItem.setUsername(userName);
            cartItem.save();
        } else {
            Log.w(DBFunction.TAG, "未找到该用户，商品添加购物车失败， userName: " + userName);
        }
    }

    public static void delCart(String userName, long commodityId) {
        User user = findUserByName(userName);
        if (user != null) {
            // 查找该用户的购物车记录
            List<CartItem> cartItems = LitePal.where("username = ? and commodityId = ?", userName, String.valueOf(commodityId)).find(CartItem.class);
            if (!cartItems.isEmpty()) {
                // 删除第一条找到的记录
                LitePal.delete(CartItem.class, cartItems.get(0).getId());
            } else {
                Log.w(DBFunction.TAG, "未找到该用户的购物车记录，cancelCart失败");
            }
        } else {
            Log.w(DBFunction.TAG, "不存在用户名为 " + userName + " 的用户");
        }
    }

    // 消息记录

    public static Message addChatMessage(String content, long sendId, long recId, String name) {
        Message cm = new Message();
        cm.setRead(false);
        cm.setContent(content);
        cm.setTimestamp(System.currentTimeMillis());
        cm.setReceiverId(recId);
        cm.setSenderId(sendId);
        cm.setReceiverName(name);
        cm.save();
        return cm;
    }

    public static List<Message> findChatHistoryByChatId(String recId, String sendId) {
        List<Message> cms = LitePal.where("receiverId = ? and senderId = ?",
                recId, sendId).find(Message.class);
        List<Message> cms1 = LitePal.where("receiverId = ? and senderId = ?",
                sendId, recId).find(Message.class);
        List<Message> result = new ArrayList<>();
        result.addAll(cms1);
        result.addAll(cms);
        result.sort(Comparator.comparing(Message::getTimestamp));
        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result;
        }
    }

    // 收藏管理

    //地址管理
    public static void addAddress(String userName, String address) {
        User user = findUserByName(userName);
        if (user != null) {
            user.addAddress(address);
            user.save();
        } else {
            Log.w(DBFunction.TAG, "未找到该用户，添加地址失败， userName: " + userName);
        }
    }

    public static void changeAddress(String userName, ArrayList<String> addresses) {
        User user = findUserByName(userName);
        if (user != null) {
            user.setAddress(addresses);
            user.save();
        } else {
            Log.w(DBFunction.TAG, "未找到该用户，添加地址失败， userName: " + userName);
        }
    }

    public static void delAddress(String userName, int index) {
        User user = findUserByName(userName);
        if (user != null) {
            // 查找该用户的收藏记录
            if (user.getAddress().size() > index) {
                Address address = Address.parseAddressFromString(user.getAddress().get(index));
                user.delAddress(index);
                user.save();
                // 如果删除的地址是默认地址，则将当前第一个地址设为默认地址
                if (address.isDefault()) {
                    ArrayList<String> addressList = user.getAddress();
                    if (!addressList.isEmpty()) {
                        Address address1 = Address.parseAddressFromString(addressList.get(0));
                        address1.setDefault(true);
                        user.changeAddress(0, address1.toString());
                        user.save();
                    }
                }
            } else {
                Log.w(DBFunction.TAG, "索引超出");
            }
        } else {
            Log.w(DBFunction.TAG, "不存在用户名为 " + userName + " 的用户");
        }
    }

    public static ArrayList<String> getAddress(String userName) {
        User user = findUserByName(userName);
        if (user != null) {
            // 查找该用户的收藏记录
            return user.getAddress();
        } else {
            Log.w(DBFunction.TAG, "不存在用户名为 " + userName + " 的用户");
        }
        return new ArrayList<>();
    }

    // 获取全部联系人
    public static List<Contact> getAllContacts(String userId) {
        return LitePal.where("userId = ?", userId).find(Contact.class);
    }

    public static void saveLastMsg(Message message) {
        // 保存发送者的消息
        Contact contact = LitePal.where("contactsId = ? and userId = ?",
                String.valueOf(message.getReceiverId()), String.valueOf(message.getSenderId())).findLast(Contact.class);
        if (contact != null) {
            contact.setLastContent(message.getContent());
            contact.save();
        } else {
            Contact newContact = new Contact();
            newContact.setUserId(message.getSenderId()); // 发送者是user
            newContact.setContactsId(message.getReceiverId()); // 接收者是contacts
            newContact.setContactsName(message.getReceiverName());
            newContact.setLastContent(message.getContent());
            newContact.save();
        }
        // 保存接受者的消息
        Contact contact1 = LitePal.where("contactsId = ? and userId = ?",
                String.valueOf(message.getSenderId()), String.valueOf(message.getReceiverId())).findLast(Contact.class);
        if (contact1 != null) {
            contact1.setLastContent(message.getContent());
            contact1.save();
        } else {
            Contact newContact = new Contact();
            newContact.setUserId(message.getReceiverId()); // 接收者是user
            newContact.setContactsId(message.getSenderId()); // 发送者是contacts
            newContact.setContactsName(MainActivity.getCurrentUsername());
            newContact.setLastContent(message.getContent());
            newContact.save();
        }
    }

    public static void addCommentWithImage(float rating,long commodityId, String userName, String title, String description, String date, String imageResource) {
        Comment comment = new Comment();
        comment.setStar(rating);
        comment.setCommodityId(commodityId);
        comment.setUserName(userName);
        comment.setTitle(title);
        comment.setDate(date);
        comment.setDescription(description);
        comment.setImageResource(imageResource);
        comment.save();
    }

    public static void addCommentNoImage(float rating,long commodityId, String userName, String title, String description, String date) {
        Comment comment = new Comment();
        comment.setStar(rating);
        comment.setCommodityId(commodityId);
        comment.setUserName(userName);
        comment.setTitle(title);
        comment.setDate(date);
        comment.setDescription(description);
        comment.setImageResource("");
        comment.save();
    }

    public static List<Comment> findCommentsByCommodityId(long commodityId) {
        return LitePal.where("commodityId = ?", String.valueOf(commodityId)).find(Comment.class);
    }

    public static List<Comment> findCommentsByUserName(String userName) {
        return LitePal.where("userName = ?", userName).find(Comment.class);
    }

    public static void delComment(String userName, long commodityId) {
        List<Comment> comments = LitePal.where("userName = ? and commodityId = ?",
                userName, String.valueOf(commodityId)).find(Comment.class);
        if (comments.isEmpty()) {
            Log.w(DBFunction.TAG, "未找到相关评论，删除评论失败");
        } else {
            for (Comment comment : comments) {
                LitePal.delete(Comment.class, comment.getId());
            }
        }
    }

    // order
    public static OrderTable addBuyOrder(long commodityId, long userId, String title, Float prize, String imgUrl, int quantity) {
        OrderTable order = new OrderTable();
        order.setCommodityId(commodityId);
        order.setCommodityPrice(prize);
        order.setCommodityStatus("待发货");
        order.setUserId(userId);
        order.setCommodityName(title);
        order.setImageUrl(imgUrl);
        order.setCommodityNum(quantity);
        order.save();
        return order;
    }

    public static void delOrder(long orderId) {
        OrderTable orderTable = LitePal.find(OrderTable.class, orderId);  // 查找 ID 为 1 的记录
        if (orderTable != null) {
            orderTable.delete();  // 删除该记录
        }
    }

    public static List<OrderTable> getOrdersFromUser(long userId) {
        List<OrderTable> orderList = LitePal.where("userId = ?", String.valueOf(userId)).find(OrderTable.class);
        if (orderList.isEmpty()) {
            Log.w(DBFunction.TAG, "订单为空");
        }
        return orderList;
    }

    public static void changeOrderStatus(long id, String status) {
        OrderTable orderTable = LitePal.find(OrderTable.class, id);
        orderTable.setCommodityStatus(status);
        orderTable.save();
    }

    // notification
    public static void sendNotification2Seller(String title, long orderId, long recipientId, long senderId, String message) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setRecipientId(recipientId);
        notification.setSenderId(senderId);
        notification.setMessage(message);
        notification.setOrderId(orderId);
        notification.save();
    }

    public static void changeCommodityStatus(long notificationId, String message) {
        Notification notification = LitePal.find(Notification.class, notificationId);
        notification.setMessage(message);
        notification.save();
    }

    public static void delNotification(long id) {
        Notification notification = LitePal.find(Notification.class, id);
        if (notification != null) {
            notification.delete();
        }
    }

    // 未发货
    public static List<Notification> getUnshippedNotification(long recipientId) {
        List<Notification> notifications = LitePal.where("recipientId = ? and message = ?",
                String.valueOf(recipientId), "待发货").find(Notification.class);
        if (notifications.isEmpty()) {
            Log.w(DBFunction.TAG, "待发货为空");
        }
        return notifications;
    }

    public static List<Notification> getShippedNotification(long recipientId) {
        List<Notification> notifications = LitePal.where("recipientId = ? and message = ?",
                String.valueOf(recipientId), "已发货").find(Notification.class);
        if (notifications.isEmpty()) {
            Log.w(DBFunction.TAG, "已发货为空");
        }
        return notifications;
    }

}
