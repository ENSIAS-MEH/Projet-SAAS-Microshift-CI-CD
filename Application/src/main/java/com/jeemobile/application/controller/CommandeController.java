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

    private static final String REDIRECT_COMMANDES = "redirect:/commandes";

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

        Map<String, Object> etatCounts = new LinkedHashMap<>();
        long maxEtatCount = 0;
        for (EtatCommande e : EtatCommande.values()) {
            long count = commandes.stream().filter(c -> c.getEtat() == e).count();
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("count", count);
            data.put("pct", 0);
            etatCounts.put(e.name(), data);
            if (count > maxEtatCount) maxEtatCount = count;
        }
        long finalMax = maxEtatCount;
        etatCounts.forEach((k, v) -> {
            Map<String, Object> data = (Map<String, Object>) v;
            long count = (Long) data.get("count");
            data.put("pct", finalMax > 0 ? Math.round(count * 100.0 / finalMax) : 0);
        });
        model.addAttribute("etatData", etatCounts);

        Map<String, Object> produitData = new LinkedHashMap<>();
        BigDecimal maxProduitTotal = BigDecimal.ZERO;
        for (CommandeResponseDTO c : commandes) {
            produitData.putIfAbsent(c.getProduitNom(), new BigDecimal[]{BigDecimal.ZERO});
            BigDecimal[] total = (BigDecimal[]) produitData.get(c.getProduitNom());
            total[0] = total[0].add(c.getPrixTotal());
            if (total[0].compareTo(maxProduitTotal) > 0) maxProduitTotal = total[0];
        }
        BigDecimal finalMaxProd = maxProduitTotal;
        Map<String, Object> produitFinal = new LinkedHashMap<>();
        produitData.forEach((k, v) -> {
            BigDecimal total = ((BigDecimal[]) v)[0];
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("total", total);
            data.put("pct", finalMaxProd.compareTo(BigDecimal.ZERO) > 0
                    ? total.multiply(BigDecimal.valueOf(100)).divide(finalMaxProd, 0, RoundingMode.HALF_UP).longValue()
                    : 0);
            produitFinal.put(k, data);
        });
        model.addAttribute("produitData", produitFinal);

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
        return REDIRECT_COMMANDES;
    }

    @PostMapping("/{id}/etat")
    public String changerEtat(@PathVariable Integer id, @RequestParam EtatCommande etat) {
        commandeService.modifierEtat(id, etat);
        return REDIRECT_COMMANDES;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        commandeService.supprimer(id);
        return REDIRECT_COMMANDES;
    }
}
