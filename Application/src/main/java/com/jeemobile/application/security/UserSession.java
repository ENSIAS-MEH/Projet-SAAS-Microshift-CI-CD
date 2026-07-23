package com.jeemobile.application.security;

import com.jeemobile.application.entity.Roles;
import com.jeemobile.application.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Point d'accès centralisé aux informations de l'utilisateur connecté,
 * équivalent au "UserSessionBean" du cahier des charges original (JSF),
 * mais adapté à Spring Security : lit le contexte de sécurité au lieu
 * de maintenir un état de session manuel.
 *
 * Utilisable depuis les contrôleurs, les vues Thymeleaf (via injection
 * ou expression SpEL), et les autres modules (1 et 2) pour les
 * vérifications de permissions.
 */
@Component
public class UserSession {

    /**
     * Retourne l'utilisateur actuellement connecté, ou null si personne
     * n'est authentifié (ex: appel depuis un contexte anonyme).
     */
    public User getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return null;
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getUser();
    }

    public boolean estConnecte() {
        return getUtilisateurConnecte() != null;
    }

    public Integer getUserId() {
        User user = getUtilisateurConnecte();
        return user != null ? user.getId() : null;
    }

    public Integer getUsineId() {
        User user = getUtilisateurConnecte();
        return (user != null && user.getUsine() != null) ? user.getUsine().getId() : null;
    }

    public String getUsername() {
        User user = getUtilisateurConnecte();
        return user != null ? user.getUsername() : null;
    }

    public boolean isAdmin() {
        User user = getUtilisateurConnecte();
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }

    private Roles getRoles() {
        User user = getUtilisateurConnecte();
        return user != null ? user.getRoles() : null;
    }

    public boolean peutAjouterCommande() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutAjouterCommande());
    }

    public boolean peutModifierCommande() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutModifierCommande());
    }

    public boolean peutAnnulerCommande() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutAnnulerCommande());
    }

    public boolean peutAjouterProduit() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutAjouterProduit());
    }

    public boolean peutModifierProduit() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutModifierProduit());
    }

    public boolean peutSupprimerProduit() {
        Roles roles = getRoles();
        return roles != null && Boolean.TRUE.equals(roles.getPeutSupprimerProduit());
    }
}