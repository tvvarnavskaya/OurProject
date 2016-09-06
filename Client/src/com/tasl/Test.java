package com.tasl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tasl.service.SendLoginService;
import com.tasl.service.SendLoginServiceImpl;

/**
 * Created by tvarnavskaya on 05.09.2016.
 */
public class Test {

    public static void main(String[] args) {
        SendLoginService sendLoginService = new SendLoginServiceImpl();
        boolean isValid = false;
        try {
            isValid = sendLoginService.sendLogin("test", "");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("my isValid="+isValid);
    }
}
