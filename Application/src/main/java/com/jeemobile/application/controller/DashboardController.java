package com.jeemobile.application.controller;

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

    public DashboardController(DashboardService dashboardService, ProduitService produitService) {
        this.dashboardService = dashboardService;
        this.produitService = produitService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("dashboard", dashboardService.getDashboard(null));
        model.addAttribute("totalProduits", dashboardService.getTotalProduits());
        model.addAttribute("produits", produitService.findAll());
        return "dashboard";
    }
}
