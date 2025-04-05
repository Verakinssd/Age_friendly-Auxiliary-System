package com.example.myapplication.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class PersonalInfoActivity extends AppCompatActivity {
    private ImageView profileImage;
    private Button changeAvatarButton;
    private Uri imageUri;
    private EditText phoneNumberEdit;
    private EditText personalitySignEdit;
    private EditText birthdayEdit;
    private RadioGroup sexRadioGroup;
    private TextView registerTimeText;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        // 初始化控件
        initViews();
        // 加载用户数据
        loadUserData();
        // 设置监听器
        setupListeners();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profile_image);
        changeAvatarButton = findViewById(R.id.change_avatar_button);
        phoneNumberEdit = findViewById(R.id.phone_number_edit);
        personalitySignEdit = findViewById(R.id.personality_sign_edit);
        birthdayEdit = findViewById(R.id.birthday_edit);
        sexRadioGroup = findViewById(R.id.sex_radio_group);
        registerTimeText = findViewById(R.id.register_time_text);
        TextView usernameText = findViewById(R.id.username_text);
        Button saveButton = findViewById(R.id.save_button);
        Button backButton = findViewById(R.id.back_button);

        // 设置用户名
        usernameText.setText(MainActivity.getCurrentUsername());

        // 设置按钮点击事件
        saveButton.setOnClickListener(v -> saveUserInfo());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        user = DBFunction.findUserByName(MainActivity.getCurrentUsername());
        if (user != null) {
            // 加载头像
            String userImageUrl = user.getImageUrl();
            if (userImageUrl != null && !userImageUrl.isEmpty()) {
                byte[] decodedString = Base64.decode(userImageUrl, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                profileImage.setImageBitmap(decodedByte);
            }

            // 加载其他信息
            phoneNumberEdit.setText(user.getPhoneNumber());
            personalitySignEdit.setText(user.getPersonalitySign());
            birthdayEdit.setText(user.getBirthday());
            registerTimeText.setText(user.getRegisterTime());

            // 设置性别
            if ("男".equals(user.getSex())) {
                ((RadioButton) findViewById(R.id.male_radio)).setChecked(true);
            } else if ("女".equals(user.getSex())) {
                ((RadioButton) findViewById(R.id.female_radio)).setChecked(true);
            }
        }
    }

    private void setupListeners() {
        // 头像更改按钮
        changeAvatarButton.setOnClickListener(v -> openImagePicker());

        // 生日选择器
        birthdayEdit.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d%02d%02d", year, month + 1, dayOfMonth);
                    birthdayEdit.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), 1);
    }

    private void saveUserInfo() {
        if (user != null) {
            // 保存用户信息
            user.setPhoneNumber(phoneNumberEdit.getText().toString());
            user.setPersonalitySign(personalitySignEdit.getText().toString());
            user.setBirthday(birthdayEdit.getText().toString());
            
            // 获取性别选择
            int selectedId = sexRadioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.male_radio) {
                user.setSex("男");
            } else if (selectedId == R.id.female_radio) {
                user.setSex("女");
            }

            // 保存到数据库
            if (user.save()) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            String filepath = getRealPathFromURI(imageUri);
            profileImage.setImageURI(imageUri);
            String base = encodeImageToBase64(filepath);
            MainActivity.setCurrentImageUrl(base);
            if (user != null) {
                user.setImageUrl(base);
                user.save();
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    public static String encodeImageToBase64(String imagePath) {
        try {
            FileInputStream imageStream = new FileInputStream(imagePath);
            byte[] imageBytes = new byte[(int) imageStream.available()];
            imageStream.read(imageBytes);
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}