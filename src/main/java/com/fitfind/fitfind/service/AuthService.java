package com.fitfind.fitfind.service;

import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private ClientRepository clientRep;

    public AuthService(ClientRepository clientRep){
        this.clientRep = clientRep;
    }

    public void login(LoginRequest req) {

    }

}
