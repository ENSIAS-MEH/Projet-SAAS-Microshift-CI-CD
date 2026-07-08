package com.jeemobile.application.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
    private long quantiteTotale;
    private BigDecimal valeurTotaleStock;
    private int nombreProduitsEnRupture;
    private List<ProduitResponseDTO> produitsEnRupture;

    public DashboardDTO(long quantiteTotale, BigDecimal valeurTotaleStock,
                        int nombreProduitsEnRupture, List<ProduitResponseDTO> produitsEnRupture) {
        this.quantiteTotale = quantiteTotale;
        this.valeurTotaleStock = valeurTotaleStock;
        this.nombreProduitsEnRupture = nombreProduitsEnRupture;
        this.produitsEnRupture = produitsEnRupture;
    }

    public long getQuantiteTotale() { return quantiteTotale; }
    public BigDecimal getValeurTotaleStock() { return valeurTotaleStock; }
    public int getNombreProduitsEnRupture() { return nombreProduitsEnRupture; }
    public List<ProduitResponseDTO> getProduitsEnRupture() { return produitsEnRupture; }
}
