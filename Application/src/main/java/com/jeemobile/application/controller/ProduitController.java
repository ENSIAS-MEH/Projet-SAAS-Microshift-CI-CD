package com.jeemobile.application.controller;

import com.jeemobile.application.entity.Produit;
import com.jeemobile.application.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("produit", new Produit());
        return "produits/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("produit", new Produit());
        return "produits/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Produit produit, BindingResult result) {
        if (result.hasErrors()) {
            return "produits/form";
        }
        produitService.save(produit);
        return "redirect:/produits";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produitService.findById(id));
        return "produits/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        produitService.delete(id);
        return "redirect:/produits";
    }
}
