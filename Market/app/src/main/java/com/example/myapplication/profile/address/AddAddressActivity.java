package com.example.myapplication.profile.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Tools;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.profile.cart.CartAdapter;

public class AddAddressActivity extends Activity {
    private EditText editTextName, editTextPhone, editTextDetailAddress;
    private CheckBox checkBoxDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDetailAddress = findViewById(R.id.editTextDetailAddress);
        checkBoxDefault = findViewById(R.id.checkBoxDefault);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            // 获取用户输入的地址信息
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String detailAddress = editTextDetailAddress.getText().toString();

            boolean isDefault = checkBoxDefault.isChecked();

            // 判断是否有空字段
            if (name.isEmpty() || phone.isEmpty() || detailAddress.isEmpty()) {
                Toast.makeText(AddAddressActivity.this, "请填写完整的地址信息", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: 保存地址到数据库
                Address newAddress = new Address(name,detailAddress, phone, isDefault);
                //newAddress.save(); // 假设使用 LitePal
                if (isDefault) {
                    Address.setAllAddressFalse(DBFunction.getAddress(MainActivity.getCurrentUsername()));
                }
                DBFunction.addAddress(MainActivity.getCurrentUsername(), newAddress.toString());
                Tools.toastMessageShort(AddAddressActivity.this, "添加地址成功!");
                // 返回上一页面
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_address", newAddress.toString());  // 把新的地址传递回去
                setResult(RESULT_OK, resultIntent);  // 设置返回结果为成功
                finish();  // 结束当前 Activity，返回上一个 Activity
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_address", "");
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
