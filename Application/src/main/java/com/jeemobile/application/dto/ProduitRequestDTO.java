package com.jeemobile.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ProduitRequestDTO {

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;

    @NotNull(message = "La quantité est obligatoire")
    @PositiveOrZero(message = "La quantité ne peut pas être négative")
    private Integer quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @PositiveOrZero(message = "Le prix unitaire ne peut pas être négatif")
    private BigDecimal prixUnitaire;

    private String description;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
