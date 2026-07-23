package com.jeemobile.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrôleur de la page de connexion.
 * Spring Security gère lui-même le traitement du formulaire
 * (POST /login) via SecurityConfig ; ce contrôleur ne fait
 * qu'afficher la page et transmettre les messages d'état
 * (erreur, déconnexion, session expirée) à la vue.
 */
@Controller
public class LoginController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String afficherLogin(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            @RequestParam(name = "expired", required = false) String expired,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Identifiants invalides ou compte verrouillé.");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "Vous avez été déconnecté avec succès.");
        }
        if (expired != null) {
            model.addAttribute("infoMessage", "Votre session a expiré, veuillez vous reconnecter.");
        }

        return "login";
    }
}