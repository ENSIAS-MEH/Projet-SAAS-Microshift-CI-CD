package com.usine.saas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardDTO {
    private long quantiteTotale;
    private BigDecimal valeurTotaleStock;
    private int nombreProduitsEnRupture;
    private List<ProduitResponseDTO> produitsEnRupture;
}
