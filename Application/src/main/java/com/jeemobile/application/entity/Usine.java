package com.jeemobile.application.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "usine")
public class Usine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "namespace_k8s", unique = true, length = 100)
    private String namespaceK8s;

    @Column(name = "actif", nullable = false)
    private Boolean actif = Boolean.TRUE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public Usine() {
    }

    public Usine(String nom, String namespaceK8s) {
        this.nom = nom;
        this.namespaceK8s = namespaceK8s;
        this.actif = Boolean.TRUE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.actif == null) {
            this.actif = Boolean.TRUE;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNamespaceK8s() {
        return namespaceK8s;
    }

    public void setNamespaceK8s(String namespaceK8s) {
        this.namespaceK8s = namespaceK8s;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usine)) return false;
        Usine usine = (Usine) o;
        return id != null && id.equals(usine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usine{id=" + id + ", nom='" + nom + "', namespaceK8s='" + namespaceK8s + "', actif=" + actif + "}";
    }
}