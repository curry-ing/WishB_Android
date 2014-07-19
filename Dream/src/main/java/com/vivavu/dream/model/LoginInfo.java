package com.vivavu.dream.model;

import com.vivavu.dream.model.user.User;

/**
 * Created by yuja on 14. 1. 8.
 */
public class LoginInfo extends User{
    private String password;
    private String command;
	private String birthday;
    private boolean isLogin = false;

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

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
}
