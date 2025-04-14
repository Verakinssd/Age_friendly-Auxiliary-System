package com.example.myapplication.home;

import static com.example.myapplication.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.chat.ChatListActivity;
import com.example.myapplication.profile.ProfileActivity;
import com.example.myapplication.square.CommodityListActivity;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.Choice;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;

public class HomepageActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    ClientV4 client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_homepage);

        String apiKey = getString(string.api_key);
        client = new ClientV4.Builder(apiKey)
                .enableTokenCache()
                .networkConfig(300, 100, 100, 100, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 1, TimeUnit.SECONDS))
                .build();

        EditText editQueryInput = findViewById(id.edit_query_input);
        Button buttonCallApi = findViewById(id.button_call_api);

        buttonCallApi.setOnClickListener(v -> {
            String query = editQueryInput.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(HomepageActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            LayoutInflater inflater = LayoutInflater.from(HomepageActivity.this);
            View popupView = inflater.inflate(layout.popup_container, null);

            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);

            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            TextView tvPopup = popupView.findViewById(id.tv_popup_text);
            Button btnClose = popupView.findViewById(id.btn_close_popup);

            btnClose.setOnClickListener(view -> popupWindow.dismiss());
            popupView.setOnClickListener(view -> popupWindow.dismiss());

            try {
                callZhipuApi(query, tvPopup);
            } catch (JSONException e) {
                e.printStackTrace();
                tvPopup.setText("Error JSON: " + e.getMessage());
            }
        });

        Button messagesButton = findViewById(id.button_messages);
        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, ChatListActivity.class);
            startActivity(intent);
        });
        Button userButton = findViewById(id.button_profile);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        Button squareButton = findViewById(id.button_square);
        squareButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, CommodityListActivity.class);
            startActivity(intent);
        });
        Button homeButton = findViewById(id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
            startActivity(intent);
        });
    }

    private void callZhipuApi(String query, TextView tvPopup) throws JSONException {
        executorService.execute(() -> {
            try {
                ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), query);
                List<ChatMessage> messageList = new ArrayList<>();
                messageList.add(chatMessage);
                String requestId = "BUAA_Android-TradingAssistant : " + System.currentTimeMillis();

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                        .model("glm-4-flash")
                        .stream(false)
                        .invokeMethod(Constants.invokeMethod)
                        .messages(messageList)
                        .requestId(requestId)
                        .build();

                ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
                ModelData modelData = invokeModelApiResp.getData();

                Choice firstChoice = modelData.getChoices().get(0);
                String content = (String) firstChoice.getMessage().getContent();
                mainHandler.post(() -> tvPopup.setText("OUTPUT: " + content));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> tvPopup.setText("Error: 输入了与商品无关的内容，请重新输入。\n" + e.getMessage()));
            }
        });
    }
}