package com.usine.saas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
public class Produit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usine_id", nullable = false)
    private Usine usine;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false)
    private Integer quantite = 0;

    // Toujours BigDecimal pour les montants, jamais double (cf. cahier des charges)
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_maj")
    private LocalDateTime dateMaj;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreation = now;
        this.dateMaj = now;
        if (this.quantite == null) {
            this.quantite = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateMaj = LocalDateTime.now();
    }
}
