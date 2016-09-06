package com.tasl.entity.login;

import java.io.Serializable;

/**
 * Created by tvarnavskaya on 05.09.2016.
 */
public class LoginData implements Serializable {
    private final String userName;
    private final String password;

    public LoginData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
