package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.entity.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(produitRepository);
    }

    private Produit createProduit(Integer id, String nom, Integer quantite, BigDecimal prix) {
        Produit p = new Produit();
        p.setId(id);
        p.setNom(nom);
        p.setQuantite(quantite);
        p.setPrixUnitaire(prix);
        p.setDateCreation(LocalDateTime.now());
        p.setDateMaj(LocalDateTime.now());
        return p;
    }

    @Test
    void getDashboard_ShouldCalculateTotals() {
        Produit p1 = createProduit(1, "P1", 10, new BigDecimal("10.00"));
        Produit p2 = createProduit(2, "P2", 6, new BigDecimal("20.00"));
        when(produitRepository.findAll()).thenReturn(List.of(p1, p2));

        DashboardDTO result = dashboardService.getDashboard(null);

        assertEquals(16, result.getQuantiteTotale());
        assertEquals(new BigDecimal("220.00"), result.getValeurTotaleStock());
        assertEquals(0, result.getNombreProduitsEnRupture());
        assertTrue(result.getProduitsEnRupture().isEmpty());
    }

    @Test
    void getDashboard_WithLowStock_ShouldIdentifyRupture() {
        Produit p1 = createProduit(1, "P1", 2, new BigDecimal("10.00"));
        Produit p2 = createProduit(2, "P2", 10, new BigDecimal("20.00"));
        when(produitRepository.findAll()).thenReturn(List.of(p1, p2));

        DashboardDTO result = dashboardService.getDashboard(5);

        assertEquals(12, result.getQuantiteTotale());
        assertEquals(1, result.getNombreProduitsEnRupture());
        assertEquals("P1", result.getProduitsEnRupture().get(0).getNom());
    }

    @Test
    void getDashboard_WhenNoProducts_ShouldReturnZeros() {
        when(produitRepository.findAll()).thenReturn(List.of());

        DashboardDTO result = dashboardService.getDashboard(null);

        assertEquals(0, result.getQuantiteTotale());
        assertEquals(BigDecimal.ZERO, result.getValeurTotaleStock());
        assertEquals(0, result.getNombreProduitsEnRupture());
        assertTrue(result.getProduitsEnRupture().isEmpty());
    }

    @Test
    void getTotalProduits_ShouldReturnCount() {
        when(produitRepository.count()).thenReturn(5L);

        long result = dashboardService.getTotalProduits();

        assertEquals(5, result);
    }
}
