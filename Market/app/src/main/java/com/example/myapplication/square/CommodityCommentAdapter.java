package com.example.myapplication.square;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.Comment;

import java.util.List;

public class CommodityCommentAdapter extends RecyclerView.Adapter<CommodityCommentAdapter.ViewHolder> {
    private final Context context;
    private final List<Comment> commentList;

    public CommodityCommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView commentImage;
        TextView userName;
        TextView commentDescription;
        TextView commentDate;
        RatingBar commentStar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentImage = itemView.findViewById(R.id.comment_image);
            userName = itemView.findViewById(R.id.comment_title);
            commentDescription = itemView.findViewById(R.id.comment_description);
            commentDate = itemView.findViewById(R.id.comment_date);
            commentStar = itemView.findViewById(R.id.comment_rating_bar);
        }
    }

    @NonNull
    @Override
    public CommodityCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommodityCommentAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull CommodityCommentAdapter.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.userName.setText(comment.getUserName());
        holder.commentDescription.setText(comment.getDescription());
        holder.commentDate.setText(comment.getDate());
        holder.commentStar.setRating(comment.getStar());

        String imageBase64 = comment.getImageResource();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.commentImage.setImageBitmap(decodedByte);
            holder.commentImage.setVisibility(View.VISIBLE);
        } else {
            holder.commentImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
