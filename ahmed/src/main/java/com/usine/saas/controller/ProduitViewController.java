package com.usine.saas.controller;

import com.usine.saas.security.UserContext;
import com.usine.saas.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Pages serveur (Thymeleaf) pour le module Produits/Stock.
 *
 * Remarque : le cahier des charges d'origine prévoyait des pages PrimeFaces
 * (.xhtml / JSF). L'équipe ayant choisi Spring Boot + Spring Data JPA, JSF/PrimeFaces
 * ne s'intègre pas naturellement (nécessiterait un conteneur JSF type MyFaces + JoinFaces).
 * On garde donc le même découpage fonctionnel (liste, formulaire, dashboard) mais avec
 * Thymeleaf + Chart.js côté vue, et les mêmes actions/permissions côté service.
 */
@Controller
@RequestMapping("/produits")
@RequiredArgsConstructor
public class ProduitViewController {

    private final ProduitService produitService;
    private final UserContext userContext;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("produits", produitService.findAllByUsine(userContext));
        model.addAttribute("peutAjouter", userContext.isAdmin() || userContext.peutAjouterProduit());
        model.addAttribute("peutModifier", userContext.isAdmin() || userContext.peutModifierProduit());
        model.addAttribute("peutSupprimer", userContext.isAdmin() || userContext.peutSupprimerProduit());
        return "produits/liste";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", produitService.getDashboard(userContext, null));
        return "produits/dashboard";
    }
}
