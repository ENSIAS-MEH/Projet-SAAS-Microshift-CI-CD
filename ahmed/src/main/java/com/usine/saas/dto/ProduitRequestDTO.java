package com.usine.saas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
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
}
