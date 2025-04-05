package com.example.myapplication;

import static com.example.myapplication.commodity.AddCommodityActivity.encodeImageToBase64;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.myapplication.database.DBFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText inputUsername;
    private EditText inputPassword1;
    private EditText inputPassword2;
    private Button backButton;
    private final String NO_USERNAME_TOAST = "请输入用户名";
    private final String NO_PASSWORD_TOAST = "请输入密码";
    private final String NO_AGAIN_PASSWORD_TOAST = "请确认密码";
    private final String PASSWORD_NOT_SAME_TOAST = "两次输入的密码不一致, 请重新输入";
    private final String PASSWORD_TOO_SHORT_TOAST = "密码长度应至少为8个字符";
    private final String USERNAME_EXIST_TOAST = "用户名已被注册";

    private enum RegisterCheckResult {
        NO_USERNAME,
        NO_PASSWORD,
        NO_AGAIN_PASSWORD,
        PASSWORD_NOT_SAME,
        PASSWORD_TOO_SHORT, // 密码长度至少为8
        USERNAME_EXIST,
        SUCCESS
    }

    public void setOnClickListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = inputUsername.getText().toString();
                String password1 = inputPassword1.getText().toString();
                String password2 = inputPassword2.getText().toString();
                tryRegister(username, password1, password2);
            }
        });
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    public RegisterCheckResult checkRegisterUser(String username, String password1, String password2) {
        if (username.equals("")) {
            return RegisterCheckResult.NO_USERNAME;
        } else if (password1.equals("")) {
            return RegisterCheckResult.NO_PASSWORD;
        } else if (password2.equals("")) {
            return RegisterCheckResult.NO_AGAIN_PASSWORD;
        } else if (!password1.equals(password2)) {
            return RegisterCheckResult.PASSWORD_NOT_SAME;
        } else if (DBFunction.isUsernameExist(username)) {
            return RegisterCheckResult.USERNAME_EXIST;
        }
        return RegisterCheckResult.SUCCESS;
    }

    public void toastThis(String mes) {
        Tools.toastMessageShort(RegisterActivity.this, mes);
    }

    public void tryRegister(String username, String password1, String password2) {
        RegisterCheckResult res = checkRegisterUser(username, password1, password2);
        switch (res) {
            case NO_USERNAME: toastThis(NO_USERNAME_TOAST); break;
            case NO_PASSWORD: toastThis(NO_PASSWORD_TOAST); break;
            case NO_AGAIN_PASSWORD: toastThis(NO_AGAIN_PASSWORD_TOAST); break;
            case PASSWORD_NOT_SAME: toastThis(PASSWORD_NOT_SAME_TOAST); break;
            case PASSWORD_TOO_SHORT: toastThis(PASSWORD_TOO_SHORT_TOAST); break;
            case USERNAME_EXIST: toastThis(USERNAME_EXIST_TOAST); break;
            default: {
                // 跳转到登录界面
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String date = String.format("%04d-%02d-%02d", year, month, day);
                String base = encodeImageResourceToBase64(R.drawable.default_avatar);
                //TODO 加入进去 ok
                DBFunction.addUser(username, password1, date , base);
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                toastThis("注册成功!");
                finish();
            }
        }
    }

    public String encodeImageResourceToBase64(int resId) {
        try {
            InputStream inputStream = getResources().openRawResource(resId);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (IOException e) {
            Log.e("EncodeImageResourseToBase64Error : ", e.getMessage());
        }
        return null;
    }

    public void initAttribute() {
        this.registerButton = findViewById(R.id.register);
        this.inputUsername = findViewById(R.id.reg_inputUserName);
        this.inputPassword1 = findViewById(R.id.reg_inputPassword);
        this.inputPassword2 = findViewById(R.id.reg_inputPassword_again);
        this.backButton = findViewById(R.id.back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initAttribute();
        setOnClickListeners();
    }
}
