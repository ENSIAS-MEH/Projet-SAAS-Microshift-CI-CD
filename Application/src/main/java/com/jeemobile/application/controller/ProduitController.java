package com.jeemobile.application.controller;

import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private static final String REDIRECT_PRODUITS = "redirect:/produits";

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("produits", produitService.findAll());
        return "produits/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("produit", new ProduitRequestDTO());
        return "produits/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("produit") ProduitRequestDTO dto) {
        produitService.creer(dto);
        return REDIRECT_PRODUITS;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
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
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("produit") ProduitRequestDTO dto) {
        produitService.modifier(id, dto);
        return REDIRECT_PRODUITS;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        produitService.supprimer(id);
        return REDIRECT_PRODUITS;
    }
}
