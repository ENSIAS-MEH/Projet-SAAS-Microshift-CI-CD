package com.jeemobile.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean PasswordEncoder isolé dans sa propre configuration pour éviter
 * une dépendance circulaire : SecurityConfig a besoin d'un
 * AuthenticationService (pour les handlers de login), et
 * AuthenticationServiceImpl a besoin d'un PasswordEncoder — si ce
 * dernier était défini dans SecurityConfig, Spring ne pourrait pas
 * déterminer quel bean créer en premier.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}