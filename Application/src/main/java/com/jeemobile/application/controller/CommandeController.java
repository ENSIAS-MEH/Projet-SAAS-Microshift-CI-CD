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
import java.util.*;
import java.util.stream.Collectors;

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
        for (EtatCommande e : EtatCommande.values()) {
            etatCounts.put(e.name(), commandes.stream().filter(c -> c.getEtat() == e).count());
        }
        model.addAttribute("etatChartLabels", String.join(",", etatCounts.keySet()));
        model.addAttribute("etatChartData", etatCounts.values().stream().map(String::valueOf).collect(Collectors.joining(",")));

        Map<String, BigDecimal> produitTotals = new LinkedHashMap<>();
        for (CommandeResponseDTO c : commandes) {
            produitTotals.merge(c.getProduitNom(), c.getPrixTotal(), BigDecimal::add);
        }
        model.addAttribute("produitChartLabels", String.join(",", produitTotals.keySet()));
        model.addAttribute("produitChartData", produitTotals.values().stream().map(String::valueOf).collect(Collectors.joining(",")));

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
