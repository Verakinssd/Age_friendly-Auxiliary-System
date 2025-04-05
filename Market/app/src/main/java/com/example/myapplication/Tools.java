package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

public class Tools {
    //这个类用来放一些常用函数，比如展示一个信息那种

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static void toastMessageShort(Context context, String mes) {
        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show();
    }


}
