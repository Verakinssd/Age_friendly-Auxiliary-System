package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

public class Tools {
    public static void toastMessageShort(Context context, String mes) {
        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show();
    }
}
