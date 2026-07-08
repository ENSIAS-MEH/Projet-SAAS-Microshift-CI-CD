package com.usine.saas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité PARTAGÉE représentant un tenant (client) de la plateforme SaaS.
 * IMPORTANT : mettez-vous d'accord avec toute l'équipe pour n'avoir
 * qu'UNE SEULE version de cette classe dans le package com.usine.saas.entity.
 * Ne pas dupliquer cette classe dans les modules des autres membres.
 */
@Entity
@Table(name = "usine")
@Getter
@Setter
@NoArgsConstructor
public class Usine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(name = "namespace_k8s", unique = true, length = 100)
    private String namespaceK8s;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}
