package com.tasl.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasl.entity.login.LoginData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tvarnavskaya on 05.09.2016.
 */
public class LoginDataProcessor {

    public LoginData processData(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("2");
        //JSON from String to Object
        LoginData loginData = mapper.readValue(jsonData, LoginData.class);
        System.out.println("LoginData="+loginData);
        return loginData;
    }

    public boolean isValidLoginData(String login, String password) {
        //TODO realize logic with DB
        List<String> list = new ArrayList<>();
        list.add("test");list.add("12345");list.add("user");list.add("123");list.add("user1");
        return list.contains(login);
    }
}
