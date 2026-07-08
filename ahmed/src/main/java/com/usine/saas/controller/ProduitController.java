package com.usine.saas.controller;

import com.usine.saas.dto.DashboardDTO;
import com.usine.saas.dto.ProduitRequestDTO;
import com.usine.saas.dto.ProduitResponseDTO;
import com.usine.saas.security.UserContext;
import com.usine.saas.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final UserContext userContext; // fourni par le module Auth (membre 3) en prod

    @GetMapping
    public List<ProduitResponseDTO> lister() {
        return produitService.findAllByUsine(userContext);
    }

    @GetMapping("/recherche")
    public Page<ProduitResponseDTO> rechercher(
            @RequestParam(required = false) String nom,
            Pageable pageable) {
        return produitService.rechercher(userContext, nom, pageable);
    }

    @GetMapping("/{id}")
    public ProduitResponseDTO obtenir(@PathVariable Integer id) {
        return produitService.findById(userContext, id);
    }

    @GetMapping("/dashboard")
    public DashboardDTO dashboard(@RequestParam(required = false) Integer seuilRupture) {
        return produitService.getDashboard(userContext, seuilRupture);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProduitResponseDTO creer(@Valid @RequestBody ProduitRequestDTO dto) {
        return produitService.creer(userContext, dto);
    }

    @PutMapping("/{id}")
    public ProduitResponseDTO modifier(@PathVariable Integer id, @Valid @RequestBody ProduitRequestDTO dto) {
        return produitService.modifier(userContext, id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
        produitService.supprimer(userContext, id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Endpoint interne pour le module Commandes (membre 2) ----------
    // A sécuriser en interne (appel service-à-service), pas destiné au front public.

    @PostMapping("/{id}/decrementer")
    public ResponseEntity<Void> decrementerStock(@PathVariable Integer id, @RequestParam Integer quantite) {
        produitService.decrementerStock(id, quantite);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/incrementer")
    public ResponseEntity<Void> incrementerStock(@PathVariable Integer id, @RequestParam Integer quantite) {
        produitService.incrementerStock(id, quantite);
        return ResponseEntity.ok().build();
    }
}
