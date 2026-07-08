package com.usine.saas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProduitResponseDTO {
    private Integer id;
    private String nom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMaj;
}
