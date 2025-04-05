package com.example.myapplication.profile.comment;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.profile.comment.CommentAdapter;
import com.example.myapplication.database.Comment;
import com.example.myapplication.database.DBFunction;

import org.litepal.LitePal;

import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        loadComments();

        // 返回按钮
        ImageButton returnButton = findViewById(R.id.back_button);
        returnButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void initViews() {
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadComments() {
        try {
            commentList = DBFunction.findCommentsByUserName(MainActivity.getCurrentUsername());
            if (commentList != null && !commentList.isEmpty()) {
                commentAdapter = new CommentAdapter(this, commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            } else {
                Toast.makeText(this, "没有评论", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载评论失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
