package com.jeemobile.application.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_usine", columnNames = {"usine_id", "username"})
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usine_id", nullable = false)
    private Usine usine;

    @Column(name = "username", nullable = false, length = 80)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "contact", length = 50)
    private String contact;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = Boolean.FALSE;

    @Column(name = "actif", nullable = false)
    private Boolean actif = Boolean.TRUE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    @Column(name = "tentatives_echouees", nullable = false)
    private Integer tentativesEchouees = 0;

    @Column(name = "verrouille_jusqua")
    private LocalDateTime verrouilleJusqua;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Roles roles;

    public User() {
    }
    public Integer getTentativesEchouees() {
        return tentativesEchouees;
    }

    public void setTentativesEchouees(Integer tentativesEchouees) {
        this.tentativesEchouees = tentativesEchouees;
    }

    public LocalDateTime getVerrouilleJusqua() {
        return verrouilleJusqua;
    }

    public void setVerrouilleJusqua(LocalDateTime verrouilleJusqua) {
        this.verrouilleJusqua = verrouilleJusqua;
    }

    public User(Usine usine, String username, String password, String nom, String email) {
        this.usine = usine;
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.email = email;
        this.isAdmin = Boolean.FALSE;
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
        if (this.isAdmin == null) {
            this.isAdmin = Boolean.FALSE;
        }
        if (this.tentativesEchouees == null) {
            this.tentativesEchouees = 0;
        }
    }

    public void setRoles(Roles roles) {
        if (roles == null) {
            if (this.roles != null) {
                this.roles.setUser(null);
            }
        } else {
            roles.setUser(this);
        }
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usine getUsine() {
        return usine;
    }

    public void setUsine(Usine usine) {
        this.usine = usine;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
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

    public Roles getRoles() {
        return roles;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', actif=" + actif + ", isAdmin=" + isAdmin + "}";
    }
}