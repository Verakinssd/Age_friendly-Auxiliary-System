package com.example.myapplication.database;

import org.litepal.crud.LitePalSupport;

public class Contact extends LitePalSupport {
    private long id;
    private long userId;
    private long contactsId;
    private String contactsName;
    private String lastContent;

    public Contact() {

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public long getContactsId() {
        return contactsId;
    }

    public void setContactsId(long contactsId) {
        this.contactsId = contactsId;
    }

    public long getId() {
        return id;
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getContactsName() {
        return contactsName;
    }
}
