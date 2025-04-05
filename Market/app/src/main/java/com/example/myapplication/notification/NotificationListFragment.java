package com.example.myapplication.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DBFunction;
import com.example.myapplication.database.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    public static NotificationListFragment newInstance(String type) {
        NotificationListFragment fragment = new NotificationListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 根据类型加载不同的数据
        List<Notification> notifications = loadNotifications(type);
        adapter = new NotificationAdapter(notifications, this::handleAction);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Notification> loadNotifications(String type) {
        if ("seller".equals(type)) {
            return DBFunction.getUnshippedNotification(MainActivity.getCurrentUserId());
        }
        return DBFunction.getShippedNotification(MainActivity.getCurrentUserId());
    }

    private void handleAction(Notification notification, int position) {
        Toast.makeText(getContext(), "操作：" + notification.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void addNotification(Notification notification) {
        adapter.addNotification(notification);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    public void removeNotification(int position) {
        adapter.removeNotification(position);
        adapter.notifyItemRemoved(position);
    }
}

