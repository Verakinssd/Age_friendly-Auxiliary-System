package com.example.myapplication.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Message;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgView extends AppCompatActivity {
    private final List<Message> messageList = new ArrayList<>();
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private final long senderId = MainActivity.getCurrentUserId();
    private long recId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        inputText = (EditText)findViewById(R.id.input_text);
        Button send = (Button) findViewById(R.id.msgSend);
        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);    //LinearLayoutLayout即线性布局，创建对象后把它设置到RecyclerView当中
        msgRecyclerView.setLayoutManager(layoutManager);

        adapter=new MsgAdapter(messageList);
        //创建MsgAdapter的实例并将数据传入到MsgAdapter的构造函数中
        msgRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        recId = intent.getLongExtra("chat_id", 0);
        String recName = intent.getStringExtra("chat_name");
        TextView receiveName = (TextView) findViewById(R.id.username_text);
        receiveName.setText(recName);

        initMsgs(recId, senderId);

        send.setOnClickListener(new View.OnClickListener(){                 //发送按钮点击事件
            @Override public void onClick(View v) {
                String content = inputText.getText().toString();              //获取EditText中的内容
                if (!content.isEmpty()) {                                    //内容不为空则创建一个新的Msg对象，并把它添加到msgList列表中
                    Message msg = DBFunction.addChatMessage(content, senderId, recId, recName);
                    messageList.add(msg);
                    DBFunction.saveLastMsg(msg);
                    adapter.notifyItemInserted(messageList.size() - 1);           //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                    msgRecyclerView.scrollToPosition(messageList.size() - 1);     //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                    inputText.setText("");                                  //调用EditText的setText()方法将输入的内容清空
                }
            }
        });

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个页面
            }
        });
    }

    private void initMsgs(long recId, long sendId){
        messageList.addAll(DBFunction.findChatHistoryByChatId(String.valueOf(recId), String.valueOf(sendId)));
    }
}
