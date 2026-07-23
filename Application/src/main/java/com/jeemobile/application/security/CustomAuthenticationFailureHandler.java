package com.jeemobile.application.security;

import com.jeemobile.application.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * Intercepte chaque échec de connexion Spring Security pour incrémenter
 * le compteur de tentatives échouées et déclencher le verrouillage
 * si nécessaire, via AuthenticationService.
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthenticationService authenticationService;

    public CustomAuthenticationFailureHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        if (username != null && !username.isBlank()) {
            authenticationService.enregistrerEchecConnexion(username);
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}