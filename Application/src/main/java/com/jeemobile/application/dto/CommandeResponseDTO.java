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

    public CommandeResponseDTO(Integer id, Integer produitId, String produitNom,
                               Integer quantite, BigDecimal prixTotal, BigDecimal prixUnitaire,
                               EtatCommande etat, LocalDateTime dateCreation, LocalDateTime dateMaj) {
        this.id = id;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.prixUnitaire = prixUnitaire;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.dateMaj = dateMaj;
    }

    public Integer getId() { return id; }
    public Integer getProduitId() { return produitId; }
    public String getProduitNom() { return produitNom; }
    public Integer getQuantite() { return quantite; }
    public BigDecimal getPrixTotal() { return prixTotal; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public EtatCommande getEtat() { return etat; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateMaj() { return dateMaj; }
}
