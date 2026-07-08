package com.jeemobile.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProduitResponseDTO {
    private Integer id;
    private String nom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMaj;

    public ProduitResponseDTO(Integer id, String nom, Integer quantite, BigDecimal prixUnitaire,
                              String description, LocalDateTime dateCreation, LocalDateTime dateMaj) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateMaj = dateMaj;
    }

    public Integer getId() { return id; }
    public String getNom() { return nom; }
    public Integer getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public String getDescription() { return description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateMaj() { return dateMaj; }
}
