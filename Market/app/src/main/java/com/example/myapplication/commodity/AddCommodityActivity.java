package com.example.myapplication.commodity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.myapplication.InputNumberView;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Type;
import com.example.myapplication.square.CommodityListActivity;
import com.example.myapplication.square.MyImageSliderAdapter;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AddCommodityActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String first = "";
    private String base = "";
    private ArrayList<String> pictures = new ArrayList<>();
    private ViewPager2 imageView;
    private MyImageSliderAdapter adapter;
    // private MinioUtils minioUtils = new MinioUtils();
    private InputNumberView quantityView;
    private static final int MAX_QUANTITY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);

        EditText nameEditText = findViewById(R.id.editTextCommodityName);
        Spinner typeSpinner = findViewById(R.id.spinnerCommodityType);
        EditText dateEditText = findViewById(R.id.editTextReleaseDate);
        EditText descriptionEditText = findViewById(R.id.editTextCommodityDescription);
        EditText priceEditText = findViewById(R.id.editTextCommodityPrice);
        Button selectImageButton = findViewById(R.id.button_select_image);
        Button addButton = findViewById(R.id.button_add_commodity);
        ImageButton backButton = findViewById(R.id.back_button);
        imageView = findViewById(R.id.commodity_image);
        backButton.setOnClickListener(v -> {
            finish(); // 返回上一页
        });

        //设置当天日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        dateEditText.setText(currentDate);

        // 设置 Spinner 的适配器,选择Type
        ArrayAdapter<Type> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Type.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
        });

        // 初始化数量选择器
        quantityView = findViewById(R.id.commodity_quantity);
        quantityView.setMaxNum(MAX_QUANTITY);
        quantityView.setOnAmountChangeListener((view, amount) -> {
            if (amount > MAX_QUANTITY) {
                Toast.makeText(getApplicationContext(), "超过最大库存限制", Toast.LENGTH_SHORT).show();
                quantityView.setCurrentNum(MAX_QUANTITY);
            }
        });

        addButton.setOnClickListener(v -> {
            // 获取输入的商品信息
            String name = nameEditText.getText().toString();
            Type type = (Type) typeSpinner.getSelectedItem();
            String description = descriptionEditText.getText().toString();
            String priceStr = priceEditText.getText().toString().trim();
            int quantity = quantityView.getCurrentNum();

            // 验证输入
            if (name.isEmpty()){
                Toast.makeText(this, "商品名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                Toast.makeText(this, "商品描述不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (priceStr.isEmpty()) {
                Toast.makeText(this, "商品价格不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity <= 0) {
                Toast.makeText(this, "商品数量必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            Float price;
            try {
                price = Float.parseFloat(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存到数据库，添加数量参数
            DBFunction.addCommodity(name, MainActivity.getCurrentUsername(), currentDate,
                    type, price, description, first, quantity, pictures);

            // 返回首页
            Intent intent = new Intent(AddCommodityActivity.this, CommodityListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // 这里可以显示选中的图片或进行其他处理

            String filepath = getRealPathFromURI(imageUri);
            base = encodeImageToBase64(filepath);
            if (first.isEmpty()) {
                first = base;
            }
            pictures.add(base);
            adapter = new MyImageSliderAdapter(pictures);
            imageView.setAdapter(adapter);
            Log.w("from AddCommodityActivity", "url is " + imageUri);
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
            Log.w("from AddCommodityActivity", "path is " + imageUri);
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