package com.example.myapplication.home;

import static com.example.myapplication.MainActivity.getCurrentUserId;
import static com.example.myapplication.MainActivity.getCurrentUsername;
import static com.example.myapplication.R.*;
import static com.example.myapplication.database.DBFunction.findCommodityNotEmpty;
import static com.example.myapplication.database.DBFunction.findCommodityNotEmpty;
import static com.example.myapplication.database.DBFunction.getCommodity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myapplication.commodity.AddCommodityActivity;
import com.example.myapplication.R;
import com.example.myapplication.chat.ChatListActivity;
import com.example.myapplication.database.CartItem;
import com.example.myapplication.database.Commodity;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Hobby;
import com.example.myapplication.database.OrderTable;
import com.example.myapplication.database.Type;
import com.example.myapplication.profile.ProfileActivity;
import com.example.myapplication.square.CommodityAdapter;
import com.example.myapplication.square.CommodityListActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.Choice;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import okhttp3.ConnectionPool;
import okhttp3.Response;

public class HomepageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView textApiResult;
    private TextView textRecommendations;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private List<Commodity> recommendList = new ArrayList<>();
    private List<Commodity> queryList = new ArrayList<>();
    private CommodityAdapter commodityAdapter;
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
        textApiResult = findViewById(id.text_api_result);
        textRecommendations = findViewById(id.text_recommendation);

        recyclerView = findViewById(id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commodityAdapter = new CommodityAdapter(this, recommendList, REQUEST_CODE); // 创建适配器
        recyclerView.setAdapter(commodityAdapter); // 设置适配器

        recyclerView1 = findViewById(id.recycler_view_query_result);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        commodityAdapter = new CommodityAdapter(this, queryList, REQUEST_CODE); // 创建适配器
        recyclerView1.setAdapter(commodityAdapter); // 设置适配器

        SensitiveWordManager sensitiveWordManager = new SensitiveWordManager(this);
        String[] wordFiles = {"COVID-19词库.txt", "其他词库.txt",
                 "民生词库.txt", "补充词库.txt", "贪腐词库.txt", "零时-Tencent.txt"};
        for (String file : wordFiles) {
            sensitiveWordManager.loadSensitiveWords(file);
        }
        //sensitiveWordTEST(sensitiveWordManager);

        //List<Commodity> allCommodities = LitePal.findAll(Commodity.class);
        List<Commodity> allCommodities = findCommodityNotEmpty();
        for (Commodity commodity : allCommodities) {
            Log.e("commodity : ", commodity.getCommodityName());
        }
        StringBuilder queryPrompt = new StringBuilder();
        if (!allCommodities.isEmpty()) {
            JSONObject queryJson = null;
            try {
                queryJson = buildQueryJson(allCommodities);
            } catch (JSONException e) {
                textApiResult.setText("Error JSON: " + e.getMessage());
                Log.e("queryJSON ERROR ", e.getMessage());
            }
            try {
                queryPrompt.append(buildQueryPrompt(queryJson));
            } catch (JSONException e) {
                textApiResult.setText("Error JSON: " + e.getMessage());
                Log.e("queryPrompt ERROR ", e.getMessage());
            }
        }

        buttonCallApi.setOnClickListener(v -> {
            String query = editQueryInput.getText().toString();
            if (query.isEmpty()) {
                Toast.makeText(HomepageActivity.this, "输入不能为空" , Toast.LENGTH_SHORT).show();
                return;
            }
            queryPrompt.append(query);
            if (sensitiveWordManager.containsSensitiveWord(query)) {
                Toast.makeText(HomepageActivity.this, "输入内容包含敏感信息，请修改后再试",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                callZhipuApi(queryPrompt.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                textApiResult.setText("Error JSON: " + e.getMessage());
            }
        });

        if (findCommodityNotEmpty().isEmpty()) {
            textRecommendations.setText("商品列表中暂时为空");
        }
        else {
            textRecommendations.setText("正在为您推荐商品：\n");
            String userName = getCurrentUsername();
            Long userId = getCurrentUserId();

            // 收藏列表
            List<Hobby> hobbies = DBFunction.findHobbyByName(userName);
            // 购物车项列表
            List<CartItem> cartItems = DBFunction.getCart(userName);
            // 订单列表
            List<OrderTable> orderTables = DBFunction.getOrdersFromUser(userId);
            List<Commodity> hobbyCommodities = new ArrayList<>();
            List<Commodity> cartItemCommodities = new ArrayList<>();
            List<Commodity> orderTableCommodities = new ArrayList<>();
            if (hobbies != null) {
                for (Hobby hobby : hobbies) {
                    hobbyCommodities.add(getCommodity(hobby.getCommodityId()));
                }
            }
            if (cartItems != null) {
                for (CartItem cartItem : cartItems) {
                    cartItemCommodities.add(getCommodity(cartItem.getCommodityId()));
                }
            }
            for (OrderTable orderTable : orderTables) {
                orderTableCommodities.add(getCommodity(orderTable.getCommodityId()));
            }

            JSONObject userFeatureJSON = null;

            try {
                userFeatureJSON = buildUserFeatureJson(hobbyCommodities, cartItemCommodities, orderTableCommodities, allCommodities);
            } catch (JSONException e) {
                textRecommendations.setText("userFeatureJSON Error: " + e.getMessage());
                e.printStackTrace();
            }
            String prompt = null;
            try {
                prompt = buildModelInput(userFeatureJSON);
            } catch (JSONException e) {
                textRecommendations.setText("prompt Error: " + e.getMessage());
            }
            Log.e("prompt : ", prompt);
            // 调用模型接口，获取推荐列表
            try {
                commodityRecommendationSystem(prompt);
            } catch (JSONException e) {
                textRecommendations.setText("commodityRecommendationSystem Error: "  + e.getMessage());
                e.printStackTrace();
            }
        }
        // 这里可以设置其他初始化逻辑，比如加载数据等
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

        Button sellButton = findViewById(id.button_sell);
        sellButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, AddCommodityActivity.class);
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

    private void sensitiveWordTEST(SensitiveWordManager sensitiveWordManager) {
        String text1 = "你好，这里是学院路37号";
        Log.e("TEST1", sensitiveWordManager.containsSensitiveWord(text1) ? "YES" : "NO");
    }

    private JSONObject buildQueryJson(List<Commodity> allCommodities) throws JSONException {
        JSONObject queryJson = new JSONObject();
        JSONArray commoditiesArray = buildCommodityJsonArray(allCommodities);
        queryJson.put("系统中所有商品", commoditiesArray);
        return queryJson;
    }

    private JSONObject buildUserFeatureJson(
            List<Commodity> hobbyCommodities,
            List<Commodity> cartItemCommodities,
            List<Commodity> orderTableCommodities,
            List<Commodity> allCommodities
    ) throws JSONException {
        JSONObject userFeature = new JSONObject();
        JSONArray favoriteItems = buildCommodityJsonArray(hobbyCommodities);
        JSONArray cartItems = buildCommodityJsonArray(cartItemCommodities);
        JSONArray orderItems = buildCommodityJsonArray(orderTableCommodities);
        JSONArray allItems = buildCommodityJsonArray(allCommodities);
        userFeature.put("收藏商品", favoriteItems);
        userFeature.put("购物车中商品" , cartItems);
        userFeature.put("订单中的商品", orderItems);
        userFeature.put("喜欢的类别", getPreferredCategories
                (orderTableCommodities,cartItemCommodities,hobbyCommodities));
        userFeature.put("系统中所有商品", allItems);
        return userFeature;
    }

    private JSONArray buildCommodityJsonArray(List<Commodity> commodities) throws JSONException {
        JSONArray commodityArray = new JSONArray();
        for (Commodity commodity : commodities) {
            JSONObject commodityJson = new JSONObject();
            commodityJson.put("名字", commodity.getCommodityName());
            commodityJson.put("价格", commodity.getPrice());
            commodityJson.put("类别", commodity.getType().name());
            commodityJson.put("描述", commodity.getDescription());
            commodityJson.put("ID", commodity.getId());
            commodityArray.put(commodityJson);
        }
        return commodityArray;
    }

    private JSONArray getPreferredCategories(List<Commodity> commodities1,
                                             List<Commodity> commodities2, List<Commodity> commodities3) {
        // 用于统计各类别的购买次数
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (Commodity commodity : commodities1) {
            String category = commodity.getType().name();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        for (Commodity commodity : commodities2) {
            String category = commodity.getType().name();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        for (Commodity commodity : commodities3) {
            String category = commodity.getType().name();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        // 排序，优先选择购买频率最高的类别
        List<Map.Entry<String, Integer>> sortedCategories = new ArrayList<>(categoryCountMap.entrySet());
        sortedCategories.sort((entry1, entry2) -> entry2.getValue() - entry1.getValue());

        // 选择前 N 个类别作为偏好（这里假设最多取 3 个）
        JSONArray preferredCategories = new JSONArray();
        int maxCategories = 3;
        for (int i = 0; i < Math.min(maxCategories, sortedCategories.size()); i++) {
            preferredCategories.put(sortedCategories.get(i).getKey());
        }

        return preferredCategories;
    }

    private String buildQueryPrompt(JSONObject queryJson) throws JSONException {
        StringBuilder prompt = new StringBuilder();
        prompt.append("您是一位智能购物助手。根据系统中的商品信息，为用户的查询提供相关商品\n");
        prompt.append("下面是系统中所有商品的相关信息：\n");
        prompt.append(formatrecommendList(queryJson.getJSONArray("系统中所有商品"))).append("\n");
        prompt.append("如果用户查询的是系统中商品相关的信息，进行推荐，严格按照如下格式。如果是其他问题，正常回答即可。\n");
        prompt.append("[\n");
        prompt.append("    {\"商品ID\": \"123\"},\n");
        prompt.append("    {\"商品ID\": \"456\"}\n");
        prompt.append("]\n");
        prompt.append("请确保返回 JSON 格式化的推荐结果。商品id必须在系统中所有商品中出现。严格按照以上JSON格式，不要有任何注释。\n");
        prompt.append("如果查询的是最。。的商品，只返回一个推荐结果即可。");
        prompt.append("如果查询与商品无关内容，正常回复它的问题即可。\n");
        prompt.append("以下是用户输入的查询内容：\n");
        return prompt.toString();
    }

    private String buildModelInput(JSONObject userFeature) throws JSONException {
        StringBuilder prompt = new StringBuilder();
        prompt.append("您是智能购物助手。根据用户的行为数据和详细的产品信息，从\"系统中所有商品\"中推荐他们可能喜欢的 5 款产品。");
        prompt.append("请注意不要从\"用户的收藏\"、\"用户的购物车中商品\"、\"用户订单中商品\"中推荐，他们只是用来预测用户喜好的。");
        prompt.append("如果系统中\"系统中所有商品\"数量不足五个则推荐\"系统中所有商品\"的全部。\n");
        prompt.append("下面是产品相关数据：\n");

        prompt.append("### 用户的收藏:\n");
        prompt.append(formatrecommendList(userFeature.getJSONArray("收藏商品"))).append("\n");

        prompt.append("### 用户的购物车中商品:\n");
        prompt.append(formatrecommendList(userFeature.getJSONArray("购物车中商品"))).append("\n");

        prompt.append("### 用户订单中的商品:\n");
        prompt.append(formatrecommendList(userFeature.getJSONArray("订单中的商品"))).append("\n");

        prompt.append("### 用户喜欢的类别:\n");
        prompt.append(userFeature.getJSONArray("喜欢的类别")).append("\n\n");

        prompt.append("### 系统中所有商品:\n");
        prompt.append(formatrecommendList(userFeature.getJSONArray("系统中所有商品"))).append("\n");

        prompt.append("请注意推荐的商品只能是\"系统中所有商品\"中的，如果系统中没有，则不推荐。\n");
        prompt.append("不要重复输出商品id，如果商品数量不够不用输出五个，输出推荐结果时，遵循以下JSON格式，例如：\n");
        prompt.append("[\n");
        prompt.append("    {\"商品ID\": \"123\"},\n");
        prompt.append("    {\"商品ID\": \"456\"}\n");
        prompt.append("]\n");
        prompt.append("请确保返回 JSON 格式化的推荐结果。商品id必须在系统中所有商品中出现。严格按照以上JSON格式，不要有任何注释，数量不够可以不用五个\n");

        return prompt.toString();
    }

    private String formatrecommendList(JSONArray commodities) throws JSONException {
        StringBuilder formattedList = new StringBuilder();
        for (int i = 0; i < commodities.length(); i++) {
            JSONObject commodity = commodities.getJSONObject(i);
            formattedList.append("- 名字: ").append(commodity.getString("名字")).append("\n");
            formattedList.append("  价格: ").append(commodity.getDouble("价格")).append("\n");
            formattedList.append("  类别: ").append(commodity.getString("类别")).append("\n");
            formattedList.append("  描述: ").append(commodity.getString("描述")).append("\n");
            formattedList.append("  ID: ").append(commodity.getInt("ID")).append("\n\n");
        }
        return formattedList.toString();
    }

    private void commodityRecommendationSystem(String prompt) throws JSONException {
        executorService.execute(() -> {
            try {
                ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
                List<ChatMessage> messageList = new ArrayList<>();
                messageList.add(chatMessage);
                String requestId = String.format("BUAA_Android-TradingAssistant : " + System.currentTimeMillis());

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                        .model("glm-4-flash")
                        .stream(Boolean.FALSE)
                        .invokeMethod(Constants.invokeMethod)
                        .messages(messageList)
                        .requestId(requestId)
                        .build();

                ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
                ModelData modelData = invokeModelApiResp.getData();

                Choice firstChoice = modelData.getChoices().get(0);
                String content = (String) firstChoice.getMessage().getContent();
                Log.e("Content : " , content);
                int startIndex = content.indexOf('[');
                int endIndex = content.lastIndexOf(']');
                content = content.substring(startIndex, endIndex + 1);
                String finalContent = content;
//                mainHandler.post(() -> textRecommendations.setText("model output: " + finalContent));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonArray = mapper.readTree(finalContent);
                recommendList.clear();
                Log.e("recommendList_before : ", recommendList.toString());
//                Log.e("jsonArray : ", jsonArray.toString());
                if (jsonArray.isArray()) {
                    for (JsonNode jsonNode : jsonArray) {
                        Commodity commodity = getCommodity(Long.parseLong(jsonNode.get("商品ID").asText()));
                        recommendList.add(commodity);
                        //Log.e("NOTICE : ", commodity.toString());
                    }
                }
                Log.e("recommendList : ", recommendList.toString());
                mainHandler.post(() -> commodityAdapter.notifyDataSetChanged());
//                mainHandler.post(() -> textRecommendations.setText("CommodityRecommendationSystem : " + content));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> textRecommendations.setText("Error: " + e.getMessage()));
            }
        });
    }

    private void callZhipuApi(String query) throws JSONException {
        executorService.execute(() -> {
            try {
                ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), query);
                List<ChatMessage> messageList = new ArrayList<>();
                messageList.add(chatMessage);
                String requestId = String.format("BUAA_Android-TradingAssistant : " + System.currentTimeMillis());

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                        .model("glm-4-flash")
                        .stream(Boolean.FALSE)
                        .invokeMethod(Constants.invokeMethod)
                        .messages(messageList)
                        .requestId(requestId)
                        .build();

                ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
                ModelData modelData = invokeModelApiResp.getData();

                Choice firstChoice = modelData.getChoices().get(0);
                String content = (String) firstChoice.getMessage().getContent();
                int startIndex = content.indexOf('[');
                int endIndex = content.lastIndexOf(']');
                if (startIndex == -1 || endIndex == -1) {
                    String finalContent = content;
                    mainHandler.post(() -> textApiResult.setText("OUTPUT: " + finalContent));
                } else {
                    mainHandler.post(() -> textApiResult.setText("以下是查询结果：\n"));
                    content = content.substring(startIndex, endIndex + 1);
                    content = content.substring(startIndex, endIndex + 1);
                    String finalContent = content;
//                mainHandler.post(() -> textApiResult.setText("OUTPUT: " + finalContent));
//                mainHandler.post(() -> textRecommendations.setText("model output: " + finalContent));
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonArray = mapper.readTree(finalContent);
                    Log.e("jsonArray : ", jsonArray.toString());
                    queryList.clear();
                    Log.e("queryList_before :", queryList.toString());
                    if (jsonArray.isArray()) {
                        for (JsonNode jsonNode : jsonArray) {
                            Commodity commodity = getCommodity(Long.parseLong(jsonNode.get("商品ID").asText()));
                            queryList.add(commodity);
                            //Log.e("NOTICE : ", commodity.toString());
                        }
                    }
                    Log.e("queryList : ", queryList.toString());
                    mainHandler.post(() -> commodityAdapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> textApiResult.setText("Error: 输入了与商品无关的内容，请重新输入。\n" + e.getMessage()));
            }
        });
    }
}