package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;
import com.vivavu.dream.model.user.User;

/**
 * Created by yuja on 14. 1. 13.
 */
public class SecureToken {
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SecureToken{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
