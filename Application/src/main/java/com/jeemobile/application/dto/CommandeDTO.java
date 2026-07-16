package com.jeemobile.application.dto;

import com.jeemobile.application.entity.EtatCommande;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandeDTO implements Serializable {

    private Integer id;
    private Integer usineId;
    private Integer produitId;
    private String produitNom;
    private Integer userId;
    private Integer quantite;
    private BigDecimal prixTotal;
    private EtatCommande etat;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMaj;

    public CommandeDTO() {}

    public CommandeDTO(Integer id, Integer usineId, Integer produitId, String produitNom,
                        Integer userId, Integer quantite, BigDecimal prixTotal,
                        EtatCommande etat, LocalDateTime dateCreation, LocalDateTime dateMaj) {
        this.id = id;
        this.usineId = usineId;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.userId = userId;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.dateMaj = dateMaj;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsineId() { return usineId; }
    public void setUsineId(Integer usineId) { this.usineId = usineId; }

    public Integer getProduitId() { return produitId; }
    public void setProduitId(Integer produitId) { this.produitId = produitId; }

    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }

    public EtatCommande getEtat() { return etat; }
    public void setEtat(EtatCommande etat) { this.etat = etat; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateMaj() { return dateMaj; }
    public void setDateMaj(LocalDateTime dateMaj) { this.dateMaj = dateMaj; }
}

