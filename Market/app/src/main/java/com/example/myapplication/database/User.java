package com.example.myapplication.database;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class User extends LitePalSupport {
    private long id;
    private double money = 1000.00;
    @Column(nullable = false, unique = true)
    private String username;        //primary
    private String password;
    private String registerTime;
    private String imageUrl;
    private String phoneNumber = "";
    //private int headImage = R.drawable.user; // 头像
    private String personalitySign = ""; // 个性签名, 不超过20个字
    private String birthday = ""; //生日, yyyymmdd 的格式, 防止前导零的情况, 以字符串存
    private String sex = ""; //性别,直接用“男”,"女"就行。 ~~男用"m"表示, 女用"f"表示~~
    private ArrayList<String> addresses = new ArrayList<>(); //地址管理
    private ArrayList<Hobby> hobbies = new ArrayList<>(); // 收藏管理
    /*
        函数部分
     */
    public User() {
    }

    public User(String username) {
        User user = new User();
        user.setUsername(username); //        this.username = username;
        user.save();
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", registerTime='" + registerTime + '\'' +
                //", headImage='" + headImage + '\'' +
                ", personalitySign='" + personalitySign + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }


    // 普通 get & set 方法

    public long getId() {
        return id;
    }

    public double getMoney() {
        return money;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() { return imageUrl; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUsername(String username) {
//        if (DBFunction.isUsernameExist(username)) {
//            Log.w(DBFunction.TAG, "用户名重复");
//            return false;
//        } else if (username == null) {
//            Log.w(DBFunction.TAG, "用户名为空");
//            return false;
//        } else {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getPersonalitySign() {
        return personalitySign;
    }

    public void setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void buy(double price) {
        this.money -= price;
    }

    public void sell(double price) {
        this.money += price;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public ArrayList<String> getAddress() {
        return addresses;
    }

    public ArrayList<Hobby> getHobbies() {
        return hobbies;
    }

    public void setAddress(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(String address) {
        addresses.add(address);
    }

    public void delAddress(int index) {
        addresses.remove(index);
    }

    public void changeAddress(int index, String address) {
        addresses.set(index, address);
    }

    public void addHobby(Hobby hobby) {
        this.hobbies.add(hobby);
    }

}
