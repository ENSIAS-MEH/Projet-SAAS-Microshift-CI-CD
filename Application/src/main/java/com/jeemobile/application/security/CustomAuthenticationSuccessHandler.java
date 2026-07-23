package com.jeemobile.application.security;

import com.jeemobile.application.service.AuthenticationService;
import com.jeemobile.application.service.HistoriqueService;
import com.jeemobile.application.enums.TypeAction;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Réinitialise le compteur de tentatives échouées et journalise la
 * connexion réussie, en s'appuyant sur le UserDetailsImpl authentifié.
 */
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final HistoriqueService historiqueService;

    public CustomAuthenticationSuccessHandler(AuthenticationService authenticationService,
                                               HistoriqueService historiqueService) {
        this.authenticationService = authenticationService;
        this.historiqueService = historiqueService;
        setDefaultTargetUrl("/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        authenticationService.reinitialiserTentatives(principal.getUserId());
        historiqueService.logAction(principal.getUsineId(), principal.getUserId(),
                "Connexion réussie", TypeAction.USER);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}