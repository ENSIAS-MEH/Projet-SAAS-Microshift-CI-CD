package com.jeemobile.application.controller;

import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.exception.ProduitReferenceParCommandeException;
import com.jeemobile.application.security.UserSession;
import com.jeemobile.application.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private static final String REDIRECT_PRODUITS = "redirect:/produits";

    private final ProduitService produitService;
    private final UserSession userSession;

    public ProduitController(ProduitService produitService, UserSession userSession) {
        this.produitService = produitService;
        this.userSession = userSession;
    }

    @GetMapping
    public String list(Model model) {
        List<ProduitResponseDTO> produits = produitService.findAll();
        model.addAttribute("produits", produits);

        DashboardDTO dashboard = produitService.getDashboard(null);
        model.addAttribute("dashboard", dashboard);

        Map<String, Object> stockData = new LinkedHashMap<>();
        long lowStock = dashboard.getNombreProduitsEnRupture();
        long normalStock = produits.size() - lowStock;
        stockData.put("lowStock", lowStock);
        stockData.put("normalStock", normalStock);
        model.addAttribute("stockData", stockData);

        return "produits/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        if (!userSession.isAdmin() && !userSession.peutAjouterProduit()) {
            return REDIRECT_PRODUITS;
        }
        model.addAttribute("produit", new ProduitRequestDTO());
        return "produits/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("produit") ProduitRequestDTO dto, RedirectAttributes redirectAttributes) {
        if (!userSession.isAdmin() && !userSession.peutAjouterProduit()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée.");
            return REDIRECT_PRODUITS;
        }
        produitService.creer(dto);
        return REDIRECT_PRODUITS;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        if (!userSession.isAdmin() && !userSession.peutModifierProduit()) {
            return REDIRECT_PRODUITS;
        }
        var produit = produitService.findById(id);
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom(produit.getNom());
        dto.setQuantite(produit.getQuantite());
        dto.setPrixUnitaire(produit.getPrixUnitaire());
        dto.setDescription(produit.getDescription());
        model.addAttribute("produit", dto);
        model.addAttribute("editId", id);
        return "produits/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("produit") ProduitRequestDTO dto, RedirectAttributes redirectAttributes) {
        if (!userSession.isAdmin() && !userSession.peutModifierProduit()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée.");
            return REDIRECT_PRODUITS;
        }
        produitService.modifier(id, dto);
        return REDIRECT_PRODUITS;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!userSession.isAdmin() && !userSession.peutSupprimerProduit()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée.");
            return REDIRECT_PRODUITS;
        }
        try {
            produitService.supprimer(id);
        } catch (ProduitReferenceParCommandeException e) {
            redirectAttributes.addFlashAttribute("deleteError", e.getMessage());
            return REDIRECT_PRODUITS;
        }
        return REDIRECT_PRODUITS;
    }
}
