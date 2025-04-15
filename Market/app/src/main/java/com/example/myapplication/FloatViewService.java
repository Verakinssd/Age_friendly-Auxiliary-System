package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;

public class FloatViewService extends Service {

    private WindowManager windowManager;
    private View floatView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ClientV4 client;

    private List<String> stepList = new ArrayList<>();
    private int currentStep = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = getString(R.string.api_key);
        client = new ClientV4.Builder(apiKey)
                .enableTokenCache()
                .networkConfig(300, 100, 100, 100, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 1, TimeUnit.SECONDS))
                .build();

        LayoutInflater inflater = LayoutInflater.from(this);
        floatView = inflater.inflate(R.layout.float_view, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatView, params);

        // 设置悬浮窗可拖动效果
        floatView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatView, params);
                        return true;
                }
                return false;
            }
        });

        // 初始化按钮点击事件，在 API 调用后根据步骤更新显示
        Button btnNext = floatView.findViewById(R.id.btn_next);
        Button btnClose = floatView.findViewById(R.id.btn_close);
        Button btnPrev = floatView.findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                updateStepText();
            } else {
                Toast.makeText(FloatViewService.this, "已经是第一步", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentStep < stepList.size() - 1) {
                currentStep++;
                updateStepText();
            } else {
                Toast.makeText(FloatViewService.this, "已经是最后一步", Toast.LENGTH_SHORT).show();
                // 如果需要，也可以结束服务：stopSelf();
            }
        });
        btnClose.setOnClickListener(v -> stopSelf());
    }

    private void updateStepText() {
        TextView tvStep = floatView.findViewById(R.id.tv_popup_text);
        if (!stepList.isEmpty() && currentStep < stepList.size()) {
            tvStep.setText(stepList.get(currentStep));
        }
    }

    private List<String> parseSteps(String text) {
        List<String> steps = new ArrayList<>();
        // 移除可能的前缀标识 "OUTPUT:"，忽略大小写
        if (text.toUpperCase().startsWith("OUTPUT:")) {
            text = text.substring(7).trim();
        }
        Pattern pattern = Pattern.compile("(?=\\d+\\.\\s)");
        String[] parts = pattern.split(text);
        for (String part : parts) {
            if (part.trim().length() > 0) {
                steps.add(part.trim());
            }
        }
        // 如果未解析出步骤，则使用全文作为一个步骤
        if (steps.isEmpty()) {
            steps.add(text);
        }
        return steps;
    }

    private void callZhipuApi(final String query) throws JSONException {
        executorService.execute(() -> {
            try {
                // 构造请求消息
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

                // 模拟日志输出
                Log.d("FloatViewService", "大模型返回内容：" + content);

                // 解析大模型返回的文本为多个步骤并更新 UI
                stepList = parseSteps(content);
                currentStep = 0;
                mainHandler.post(() -> updateStepText());
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    TextView tvResult = floatView.findViewById(R.id.tv_popup_text);
                    tvResult.setText("Error: " + e.getMessage());
                });
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取从 HomepageActivity 传递的查询内容
        String query = intent.getStringExtra("query");
        StringBuilder sb = new StringBuilder();
        sb.append("查询内容： " + query + "\n");
        sb.append("返回结果格式如 1.  ....  2. ....  3. .... ， 不需要其他的内容");
        if (query != null && !query.isEmpty()) {
            try {
                callZhipuApi(query);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) {
            windowManager.removeView(floatView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}