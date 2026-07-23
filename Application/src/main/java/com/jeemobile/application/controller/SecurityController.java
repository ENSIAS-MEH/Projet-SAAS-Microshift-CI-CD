package com.jeemobile.application.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jeemobile.application.entity.Roles;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.security.UserSession;
import com.jeemobile.application.service.RoleService;
import com.jeemobile.application.service.UserService;

/**
 * Gestion des permissions (Roles) des utilisateurs de l'usine
 * connectée. Distincte de UserController : ici on modifie les
 * droits d'accès, pas les informations du compte lui-même.
 */
@Controller
@RequestMapping("/admin/securite")
public class SecurityController {

    private final RoleService roleService;
    private final UserService userService;
    private final UserSession userSession;

    public SecurityController(RoleService roleService, UserService userService, UserSession userSession) {
        this.roleService = roleService;
        this.userService = userService;
        this.userSession = userSession;
    }

    @GetMapping
    public String lister(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> utilisateurs = userService.listerParUsine(userSession.getUsineId(), pageable);
        model.addAttribute("utilisateurs", utilisateurs);
        return "admin/securite";
    }

    @GetMapping("/{userId}")
    public String afficherPermissions(@PathVariable Integer userId, Model model) {
        userService.trouverParId(userId).ifPresent(user -> model.addAttribute("user", user));
        roleService.trouverParUserId(userId).ifPresent(roles -> model.addAttribute("roles", roles));
        return "admin/securite-form";
    }

    @PostMapping("/{userId}")
    public String mettreAJourPermissions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "false") boolean peutAjouterCommande,
            @RequestParam(defaultValue = "false") boolean peutModifierCommande,
            @RequestParam(defaultValue = "false") boolean peutAnnulerCommande,
            @RequestParam(defaultValue = "false") boolean peutAjouterProduit,
            @RequestParam(defaultValue = "false") boolean peutModifierProduit,
            @RequestParam(defaultValue = "false") boolean peutSupprimerProduit,
            @RequestParam(defaultValue = "false") boolean estAdmin,
            RedirectAttributes redirectAttributes) {

        Roles roles = roleService.mettreAJourPermissions(userId,
                peutAjouterCommande, peutModifierCommande, peutAnnulerCommande,
                peutAjouterProduit, peutModifierProduit, peutSupprimerProduit, estAdmin);

        redirectAttributes.addFlashAttribute("successMessage", "Permissions mises à jour pour l'utilisateur.");
        return "redirect:/admin/securite";
    }
}