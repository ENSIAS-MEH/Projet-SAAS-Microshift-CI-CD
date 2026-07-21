package com.jeemobile.application.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommandeTest {

    @Test
    void constructor_ShouldCreateEmptyOrder() {
        Commande c = new Commande();

        assertNull(c.getId());
        assertNull(c.getProduit());
        assertNull(c.getQuantite());
        assertNull(c.getPrixTotal());
        assertNull(c.getEtat());
        assertNull(c.getDateCreation());
        assertNull(c.getDateMaj());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Commande c = new Commande();
        Produit p = new Produit();
        p.setId(1);
        LocalDateTime now = LocalDateTime.now();

        c.setId(1);
        c.setProduit(p);
        c.setQuantite(5);
        c.setPrixTotal(new BigDecimal("50.00"));
        c.setEtat(EtatCommande.VALIDEE);
        c.setDateCreation(now);
        c.setDateMaj(now);

        assertEquals(1, c.getId());
        assertEquals(p, c.getProduit());
        assertEquals(p.getId(), c.getProduit().getId());
        assertEquals(5, c.getQuantite());
        assertEquals(new BigDecimal("50.00"), c.getPrixTotal());
        assertEquals(EtatCommande.VALIDEE, c.getEtat());
        assertEquals(now, c.getDateCreation());
        assertEquals(now, c.getDateMaj());
    }

    @Test
    void onCreate_ShouldSetDatesAndDefaultEtat() {
        Commande c = new Commande();
        LocalDateTime before = LocalDateTime.now();

        c.onCreate();

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(c.getDateCreation());
        assertNotNull(c.getDateMaj());
        assertTrue(!c.getDateCreation().isBefore(before) && !c.getDateCreation().isAfter(after));
        assertEquals(EtatCommande.EN_ATTENTE, c.getEtat());
    }

    @Test
    void onCreate_WhenEtatSet_ShouldNotOverride() {
        Commande c = new Commande();
        c.setEtat(EtatCommande.VALIDEE);

        c.onCreate();

        assertEquals(EtatCommande.VALIDEE, c.getEtat());
    }

    @Test
    void onUpdate_ShouldUpdateDateMaj() {
        Commande c = new Commande();
        LocalDateTime before = LocalDateTime.now();

        c.onUpdate();

        LocalDateTime after = LocalDateTime.now();
        assertNotNull(c.getDateMaj());
        assertTrue(!c.getDateMaj().isBefore(before) && !c.getDateMaj().isAfter(after));
    }

    @Test
    void produit_Relationship_ShouldWork() {
        Produit p = new Produit();
        p.setId(1);
        p.setNom("Produit Test");

        Commande c = new Commande();
        c.setProduit(p);

        assertNotNull(c.getProduit());
        assertEquals(1, c.getProduit().getId());
        assertEquals("Produit Test", c.getProduit().getNom());
    }
}
