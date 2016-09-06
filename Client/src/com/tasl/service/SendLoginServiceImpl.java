package com.tasl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasl.entity.login.LoginData;
import com.tasl.other.SampleClient;

/**
 * Created by slaix on 04.09.2016.
 */
public class SendLoginServiceImpl implements SendLoginService {
    @Override
    public boolean sendLogin(String userName, String password) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        LoginData loginData = new LoginData(userName, password);
        //Object to JSON in String
        String jsonInString = mapper.writeValueAsString(loginData);
        System.out.println("jsonInString="+jsonInString);
        SampleClient sampleClient = new SampleClient("LoginData", "LoginData"+jsonInString);
        return sampleClient.listenSocket();
    }
}
