package com.tasl.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by slaix on 04.09.2016.
 */
public interface SendLoginService {

    boolean sendLogin(String userName, String password) throws JsonProcessingException;
}
