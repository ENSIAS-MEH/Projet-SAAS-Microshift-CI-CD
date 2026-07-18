package com.jeemobile.application.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    @Test
    void constructor_ShouldCreateEmptyProduct() {
        Produit p = new Produit();

        assertNull(p.getId());
        assertNull(p.getNom());
        assertEquals(0, p.getQuantite()); // defaults to 0
        assertNull(p.getPrixUnitaire());
        assertNull(p.getDescription());
        assertNull(p.getDateCreation());
        assertNull(p.getDateMaj());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Produit p = new Produit();
        LocalDateTime now = LocalDateTime.now();

        p.setId(1);
        p.setNom("Test");
        p.setQuantite(10);
        p.setPrixUnitaire(new BigDecimal("15.99"));
        p.setDescription("Description");
        p.setDateCreation(now);
        p.setDateMaj(now);

        assertEquals(1, p.getId());
        assertEquals("Test", p.getNom());
        assertEquals(10, p.getQuantite());
        assertEquals(new BigDecimal("15.99"), p.getPrixUnitaire());
        assertEquals("Description", p.getDescription());
        assertEquals(now, p.getDateCreation());
        assertEquals(now, p.getDateMaj());
    }

    @Test
    void onCreate_ShouldSetDatesAndDefaultQuantite() {
        Produit p = new Produit();
        LocalDateTime before = LocalDateTime.now();

        p.onCreate();

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(p.getDateCreation());
        assertNotNull(p.getDateMaj());
        assertTrue(!p.getDateCreation().isBefore(before) && !p.getDateCreation().isAfter(after));
        assertEquals(0, p.getQuantite());
    }

    @Test
    void onCreate_WhenQuantiteSet_ShouldNotOverride() {
        Produit p = new Produit();
        p.setQuantite(10);

        p.onCreate();

        assertEquals(10, p.getQuantite());
    }

    @Test
    void onUpdate_ShouldUpdateDateMaj() {
        Produit p = new Produit();
        LocalDateTime before = LocalDateTime.now();

        p.onUpdate();

        LocalDateTime after = LocalDateTime.now();
        assertNotNull(p.getDateMaj());
        assertTrue(!p.getDateMaj().isBefore(before) && !p.getDateMaj().isAfter(after));
    }
}
