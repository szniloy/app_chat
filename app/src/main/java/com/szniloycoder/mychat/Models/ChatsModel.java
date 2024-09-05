package com.szniloycoder.mychat.Models;

import android.graphics.Bitmap;

public class ChatsModel {

    private Bitmap imageBitmap;
    private boolean isseen;
    private String key;
    private String message;
    private String receiver;
    private String sender;
    private Long time;
    private String type;

    public ChatsModel() {
    }

    public ChatsModel(Bitmap imageBitmap, boolean isseen, String key, String message, String receiver, String sender, Long time, String type) {
        this.imageBitmap = imageBitmap;
        this.isseen = isseen;
        this.key = key;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
        this.type = type;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
