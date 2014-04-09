package com.vivavu.dream.model;

/**
 * Created by yuja on 14. 1. 8.
 */
public class BaseModel {
    private String email;
    private String password;
    private String command;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
