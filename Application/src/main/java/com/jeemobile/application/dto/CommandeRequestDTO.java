package com.jeemobile.application.dto;

import java.io.Serializable;


public class CommandeRequestDTO implements Serializable {

    private Integer produitId;
    private Integer quantite;

    public CommandeRequestDTO() {}

    public CommandeRequestDTO(Integer produitId, Integer quantite) {
        this.produitId = produitId;
        this.quantite = quantite;
    }

    public Integer getProduitId() { return produitId; }
    public void setProduitId(Integer produitId) { this.produitId = produitId; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}

