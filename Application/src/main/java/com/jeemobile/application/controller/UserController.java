package com.jeemobile.application.controller;

import com.jeemobile.application.exception.RessourceIntrouvableException;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.security.UserSession;
import com.jeemobile.application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestion des utilisateurs de l'usine connectée.
 * Accessible uniquement aux administrateurs (protégé par
 * SecurityConfig via /admin/**).
 */
@Controller
@RequestMapping("/admin/utilisateurs")
public class UserController {

    private final UserService userService;
    private final UserSession userSession;

    public UserController(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    @GetMapping
    public String lister(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> utilisateurs = userService.listerParUsine(userSession.getUsineId(), pageable);
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("currentUserId", userSession.getUserId());
        return "admin/utilisateurs";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("user", new User());
        return "admin/utilisateur-form";
    }

    @PostMapping
    public String creer(@RequestParam String username,
                         @RequestParam String motDePasse,
                         @RequestParam String nom,
                         @RequestParam(required = false) String contact,
                         @RequestParam String email,
                         RedirectAttributes redirectAttributes) {
        try {
            userService.creerUtilisateur(userSession.getUsineId(), username, motDePasse, nom, contact, email);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès.");
        } catch (IllegalArgumentException | RessourceIntrouvableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        userService.trouverParId(id).ifPresent(user -> model.addAttribute("user", user));
        return "admin/utilisateur-form";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Integer id,
                            @RequestParam String nom,
                            @RequestParam(required = false) String contact,
                            @RequestParam String email,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.modifierUtilisateur(id, nom, contact, email);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur modifié avec succès.");
        } catch (IllegalArgumentException | RessourceIntrouvableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/{id}/mot-de-passe")
    public String changerMotDePasse(@PathVariable Integer id,
                                     @RequestParam String nouveauMotDePasse,
                                     RedirectAttributes redirectAttributes) {
        try {
            userService.changerMotDePasse(id, nouveauMotDePasse);
            redirectAttributes.addFlashAttribute("successMessage", "Mot de passe modifié avec succès.");
        } catch (IllegalArgumentException | RessourceIntrouvableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }
    
    @PostMapping("/{id}/activer")
    public String activer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.activer(id);
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur activé.");
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/{id}/desactiver")
    public String desactiver(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.desactiver(id);
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur désactivé.");
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.supprimer(id);
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé.");
        return "redirect:/admin/utilisateurs";
    }
}