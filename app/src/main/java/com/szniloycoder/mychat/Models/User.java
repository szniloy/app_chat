package com.szniloycoder.mychat.Models;

public class User {
    String email;
    String id;
    String imageUrl;
    String userName;


    public User() {
    }

    public User(String email, String id, String imageUrl, String userName) {
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
