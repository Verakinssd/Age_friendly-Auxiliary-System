package com.example.myapplication.notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotificationPagerAdapter extends FragmentStateAdapter {

    public NotificationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return NotificationListFragment.newInstance("seller");
        } else {
            return NotificationListFragment.newInstance("buyer");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 两个Tab
    }
}

