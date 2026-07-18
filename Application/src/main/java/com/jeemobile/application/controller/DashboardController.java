package com.jeemobile.application.controller;

import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.service.CommandeService;
import com.jeemobile.application.service.DashboardService;
import com.jeemobile.application.service.ProduitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProduitService produitService;
    private final CommandeService commandeService;

    public DashboardController(DashboardService dashboardService, ProduitService produitService, CommandeService commandeService) {
        this.dashboardService = dashboardService;
        this.produitService = produitService;
        this.commandeService = commandeService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("dashboard", dashboardService.getDashboard(null));
        model.addAttribute("totalProduits", dashboardService.getTotalProduits());
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("totalCommandes", commandeService.countTotal());
        model.addAttribute("commandesEnAttente", commandeService.countByEtat(EtatCommande.EN_ATTENTE));
        model.addAttribute("commandesValidees", commandeService.countByEtat(EtatCommande.VALIDEE));
        model.addAttribute("commandes", commandeService.findAll());
        return "dashboard";
    }
}
