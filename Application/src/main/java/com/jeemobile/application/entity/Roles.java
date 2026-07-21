package com.jeemobile.application.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "peut_ajouter_commande", nullable = false)
    private Boolean peutAjouterCommande = Boolean.FALSE;

    @Column(name = "peut_modifier_commande", nullable = false)
    private Boolean peutModifierCommande = Boolean.FALSE;

    @Column(name = "peut_annuler_commande", nullable = false)
    private Boolean peutAnnulerCommande = Boolean.FALSE;

    @Column(name = "peut_ajouter_produit", nullable = false)
    private Boolean peutAjouterProduit = Boolean.FALSE;

    @Column(name = "peut_modifier_produit", nullable = false)
    private Boolean peutModifierProduit = Boolean.FALSE;

    @Column(name = "peut_supprimer_produit", nullable = false)
    private Boolean peutSupprimerProduit = Boolean.FALSE;

    @Column(name = "est_admin", nullable = false)
    private Boolean estAdmin = Boolean.FALSE;

    public Roles() {
    }

    public Roles(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    void setUser(User user) {
        this.user = user;
    }

    public Boolean getPeutAjouterCommande() {
        return peutAjouterCommande;
    }

    public void setPeutAjouterCommande(Boolean peutAjouterCommande) {
        this.peutAjouterCommande = peutAjouterCommande;
    }

    public Boolean getPeutModifierCommande() {
        return peutModifierCommande;
    }

    public void setPeutModifierCommande(Boolean peutModifierCommande) {
        this.peutModifierCommande = peutModifierCommande;
    }

    public Boolean getPeutAnnulerCommande() {
        return peutAnnulerCommande;
    }

    public void setPeutAnnulerCommande(Boolean peutAnnulerCommande) {
        this.peutAnnulerCommande = peutAnnulerCommande;
    }

    public Boolean getPeutAjouterProduit() {
        return peutAjouterProduit;
    }

    public void setPeutAjouterProduit(Boolean peutAjouterProduit) {
        this.peutAjouterProduit = peutAjouterProduit;
    }

    public Boolean getPeutModifierProduit() {
        return peutModifierProduit;
    }

    public void setPeutModifierProduit(Boolean peutModifierProduit) {
        this.peutModifierProduit = peutModifierProduit;
    }

    public Boolean getPeutSupprimerProduit() {
        return peutSupprimerProduit;
    }

    public void setPeutSupprimerProduit(Boolean peutSupprimerProduit) {
        this.peutSupprimerProduit = peutSupprimerProduit;
    }

    public Boolean getEstAdmin() {
        return estAdmin;
    }

    public void setEstAdmin(Boolean estAdmin) {
        this.estAdmin = estAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roles)) return false;
        Roles roles = (Roles) o;
        return id != null && id.equals(roles.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Roles{id=" + id + ", userId=" + (user != null ? user.getId() : null) + ", estAdmin=" + estAdmin + "}";
    }
}
