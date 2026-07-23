package com.jeemobile.application.service;

import com.jeemobile.application.entity.User;

public interface AuthenticationService {

    
    User authentifier(String username, String motDePasseEnClair);

    void enregistrerEchecConnexion(String username);

    void reinitialiserTentatives(Integer userId);
}