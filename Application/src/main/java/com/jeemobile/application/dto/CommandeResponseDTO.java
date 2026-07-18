package com.jeemobile.application.dto;

import com.jeemobile.application.entity.EtatCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandeResponseDTO {

    private Integer id;
    private Integer produitId;
    private String produitNom;
    private Integer quantite;
    private BigDecimal prixTotal;
    private BigDecimal prixUnitaire;
    private EtatCommande etat;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMaj;

    public static class Builder {
        private Integer id;
        private Integer produitId;
        private String produitNom;
        private Integer quantite;
        private BigDecimal prixTotal;
        private BigDecimal prixUnitaire;
        private EtatCommande etat;
        private LocalDateTime dateCreation;
        private LocalDateTime dateMaj;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder produitId(Integer produitId) { this.produitId = produitId; return this; }
        public Builder produitNom(String produitNom) { this.produitNom = produitNom; return this; }
        public Builder quantite(Integer quantite) { this.quantite = quantite; return this; }
        public Builder prixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; return this; }
        public Builder prixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; return this; }
        public Builder etat(EtatCommande etat) { this.etat = etat; return this; }
        public Builder dateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; return this; }
        public Builder dateMaj(LocalDateTime dateMaj) { this.dateMaj = dateMaj; return this; }

        public CommandeResponseDTO build() {
            return new CommandeResponseDTO(this);
        }
    }

    private CommandeResponseDTO(Builder builder) {
        this.id = builder.id;
        this.produitId = builder.produitId;
        this.produitNom = builder.produitNom;
        this.quantite = builder.quantite;
        this.prixTotal = builder.prixTotal;
        this.prixUnitaire = builder.prixUnitaire;
        this.etat = builder.etat;
        this.dateCreation = builder.dateCreation;
        this.dateMaj = builder.dateMaj;
    }

    CommandeResponseDTO() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProduitId() { return produitId; }
    public void setProduitId(Integer produitId) { this.produitId = produitId; }
    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public EtatCommande getEtat() { return etat; }
    public void setEtat(EtatCommande etat) { this.etat = etat; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateMaj() { return dateMaj; }
    public void setDateMaj(LocalDateTime dateMaj) { this.dateMaj = dateMaj; }
}
