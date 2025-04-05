package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

@SuppressLint("ParcelCreator")
public class mySearchSuggestion implements SearchSuggestion {
    private String suggestion;
    private boolean mIsHistory = false;

    public mySearchSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public mySearchSuggestion(Parcel source) {
        this.suggestion = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean mIsHistory) {
        this.mIsHistory = mIsHistory;
    }

    public boolean getIsHistory() {
        return mIsHistory;
    }

    @Override
    public String getBody() {
        return suggestion;
    }

    public static final Creator<mySearchSuggestion> CREATOR = new Creator<mySearchSuggestion>() {
        @Override
        public mySearchSuggestion createFromParcel(Parcel parcel) {
            return new mySearchSuggestion(parcel);
        }

        @Override
        public mySearchSuggestion[] newArray(int i) {
            return new mySearchSuggestion[i];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(suggestion);
        dest.writeInt(mIsHistory ? 1: 0);
    }

    public boolean isHistory() {
        return mIsHistory; // 根据需要返回
    }
}
