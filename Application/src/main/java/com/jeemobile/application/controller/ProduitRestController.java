package com.jeemobile.application.controller;

import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitRestController {

    private final ProduitService produitService;

    public ProduitRestController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public List<ProduitResponseDTO> getAll() {
        return produitService.findAll();
    }

    @GetMapping("/recherche")
    public Page<ProduitResponseDTO> rechercher(
            @RequestParam(required = false) String nom,
            Pageable pageable) {
        return produitService.rechercher(nom, pageable);
    }

    @GetMapping("/{id}")
    public ProduitResponseDTO getById(@PathVariable Integer id) {
        return produitService.findById(id);
    }

    @GetMapping("/dashboard")
    public DashboardDTO dashboard(@RequestParam(required = false) Integer seuilRupture) {
        return produitService.getDashboard(seuilRupture);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProduitResponseDTO create(@Valid @RequestBody ProduitRequestDTO dto) {
        return produitService.creer(dto);
    }

    @PutMapping("/{id}")
    public ProduitResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ProduitRequestDTO dto) {
        return produitService.modifier(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

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
