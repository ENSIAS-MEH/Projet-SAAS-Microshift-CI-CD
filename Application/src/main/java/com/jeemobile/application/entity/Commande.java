package com.jeemobile.application.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import jakarta.persistence.*;


@Entity
@Table(name = "commande") // 
public class Commande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer id; // [cite: 31]

  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false) 
    private Produit produit; 

    @Column(nullable = false)
    private Integer quantite; 

    @Column(name = "prix_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixTotal;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private EtatCommande etat; 

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation; 

    @Column(name = "date_maj")
    private LocalDateTime dateMaj; 

  
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now(ZoneId.systemDefault());
        this.dateMaj = LocalDateTime.now(ZoneId.systemDefault());
        if (this.etat == null) {
            this.etat = EtatCommande.EN_ATTENTE; // Default state when created [cite: 51, 61]
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateMaj = LocalDateTime.now(ZoneId.systemDefault());
    }

    // --- Constructors ---
    public Commande() {
    }




  
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }


    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateMaj() {
        return dateMaj;
    }

    public void setDateMaj(LocalDateTime dateMaj) {
        this.dateMaj = dateMaj;
    }
}