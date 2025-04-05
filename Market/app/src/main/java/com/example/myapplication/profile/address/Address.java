package com.example.myapplication.profile.address;

import com.example.myapplication.MainActivity;
import com.example.myapplication.database.DBFunction;

import java.util.ArrayList;

public class Address {
    private String name;  // 地址名称或收货人姓名
    private String detail;  // 详细地址
    private String phoneNumber;  // 联系电话
    private boolean isDefault;  // 是否为默认地址

    // 构造函数
    public Address(String name, String detail, String phoneNumber, boolean isDefault) {
        this.name = name;
        this.detail = detail;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public static void setAllAddressFalse(ArrayList<String> addressesString) {
        ArrayList<Address> addresses = new ArrayList<>();
        for (String s: addressesString) {
            addresses.add(Address.parseAddressFromString(s));
        }
        ArrayList<String> newAddresses = new ArrayList<>();
        for (Address address: addresses) {
            address.setDefault(false);
            newAddresses.add(address.toString());
        }
        DBFunction.changeAddress(MainActivity.getCurrentUsername(), newAddresses);
    }

    public static Address parseAddressFromString(String addressString) {
        // 移除 "Address{" 和 "}" 字符
        String trimmedString = addressString.replace("Address{", "").replace("}", "");
        // 通过逗号分割字段
        String[] parts = trimmedString.split(", ");
        String name = "";
        String detail = "";
        String phoneNumber = "";
        boolean isDefault = false;
        // 遍历每个字段并提取数据
        for (String part : parts) {
            if (part.startsWith("name='")) {
                name = part.substring(6, part.length() - 1);  // 去掉 'name=' 和结尾的 '
            } else if (part.startsWith("detail='")) {
                detail = part.substring(8, part.length() - 1);  // 去掉 'detail=' 和结尾的 '
            } else if (part.startsWith("phoneNumber='")) {
                phoneNumber = part.substring(13, part.length() - 1);  // 去掉 'phoneNumber=' 和结尾的 '
            } else if (part.startsWith("isDefault=")) {
                isDefault = Boolean.parseBoolean(part.substring(10));  // 获取布尔值
            }
        }
        // 返回解析后的 Address 对象
        return new Address(name, detail, phoneNumber, isDefault);
    }


    @Override
    public String toString() {
        return "Address{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}

