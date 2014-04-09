package com.vivavu.dream.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 14. 1. 8.
 */
public class UserWrapped {
    @SerializedName("user")
    private User user;

    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
