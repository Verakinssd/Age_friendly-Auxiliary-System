package com.example.myapplication.database;

public enum Type {
    BOOK(0),
    ELECTRONICS(1), //电子产品
    CLOTHING(2),
    FOOD(3),
    TOY(4),
    COSMETICS(5), //化妆品
    BAG(6),
    WATCH(7), //表
    JEWELRY(8), //首饰
    SPORTSEQUIPMENT(9),//运动器材，自行车等
    OTHERS(10);//其他

    private final int value;

    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Type getTypeByValue(int value) {
        for (Type type : Type.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
