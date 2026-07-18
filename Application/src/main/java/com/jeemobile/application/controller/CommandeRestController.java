package com.jeemobile.application.controller;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeRestController {

    private final CommandeService commandeService;

    public CommandeRestController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public List<CommandeResponseDTO> getAll() {
        return commandeService.findAll();
    }

    @GetMapping("/{id}")
    public CommandeResponseDTO getById(@PathVariable Integer id) {
        return commandeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeResponseDTO create(@Valid @RequestBody CommandeRequestDTO dto) {
        return commandeService.creer(dto);
    }

    @PutMapping("/{id}/etat")
    public CommandeResponseDTO changerEtat(@PathVariable Integer id, @RequestBody EtatCommande etat) {
        return commandeService.modifierEtat(id, etat);
    }

    @GetMapping("/produit/{produitId}")
    public List<CommandeResponseDTO> getByProduit(@PathVariable Integer produitId) {
        return commandeService.findByProduitId(produitId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        commandeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
