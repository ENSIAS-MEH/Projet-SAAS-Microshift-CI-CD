package com.jeemobile.application.controller;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.service.CommandeService;
import com.jeemobile.application.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Controller
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final ProduitService produitService;

    public CommandeController(CommandeService commandeService, ProduitService produitService) {
        this.commandeService = commandeService;
        this.produitService = produitService;
    }

    @GetMapping
    public String list(Model model) {
        List<CommandeResponseDTO> commandes = commandeService.findAll();
        model.addAttribute("commandes", commandes);
        model.addAttribute("etats", EtatCommande.values());

        Map<String, Long> etatCounts = new LinkedHashMap<>();
        long maxEtatCount = 0;
        for (EtatCommande e : EtatCommande.values()) {
            long count = commandes.stream().filter(c -> c.getEtat() == e).count();
            etatCounts.put(e.name(), count);
            if (count > maxEtatCount) maxEtatCount = count;
        }
        model.addAttribute("etatCounts", etatCounts);
        model.addAttribute("maxEtatCount", maxEtatCount);

        Map<String, BigDecimal> produitTotals = new LinkedHashMap<>();
        BigDecimal maxProduitTotal = BigDecimal.ZERO;
        for (CommandeResponseDTO c : commandes) {
            BigDecimal total = produitTotals.merge(c.getProduitNom(), c.getPrixTotal(), BigDecimal::add);
            if (total.compareTo(maxProduitTotal) > 0) maxProduitTotal = total;
        }
        model.addAttribute("produitTotals", produitTotals);
        model.addAttribute("maxProduitTotal", maxProduitTotal);

        return "commandes/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("commande", new CommandeRequestDTO());
        model.addAttribute("produits", produitService.findAll());
        return "commandes/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("commande") CommandeRequestDTO dto) {
        commandeService.creer(dto);
        return "redirect:/commandes";
    }

    @PostMapping("/{id}/etat")
    public String changerEtat(@PathVariable Integer id, @RequestParam EtatCommande etat) {
        commandeService.modifierEtat(id, etat);
        return "redirect:/commandes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        commandeService.supprimer(id);
        return "redirect:/commandes";
    }
}
