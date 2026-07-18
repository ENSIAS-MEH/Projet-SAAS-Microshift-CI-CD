package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.Produit;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.exception.ProduitReferenceParCommandeException;
import com.jeemobile.application.exception.StockInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    private ProduitService produitService;

    @BeforeEach
    void setUp() {
        produitService = new ProduitService(produitRepository);
    }

    private Produit createProduit(Integer id, String nom, Integer quantite, BigDecimal prix, String description) {
        Produit p = new Produit();
        p.setId(id);
        p.setNom(nom);
        p.setQuantite(quantite);
        p.setPrixUnitaire(prix);
        p.setDescription(description);
        p.setDateCreation(LocalDateTime.now());
        p.setDateMaj(LocalDateTime.now());
        return p;
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        List<Produit> produits = List.of(
                createProduit(1, "Produit A", 10, new BigDecimal("10.00"), "Desc A"),
                createProduit(2, "Produit B", 20, new BigDecimal("20.00"), "Desc B")
        );
        when(produitRepository.findAll()).thenReturn(produits);

        List<ProduitResponseDTO> result = produitService.findAll();

        assertEquals(2, result.size());
        assertEquals("Produit A", result.get(0).getNom());
        assertEquals("Produit B", result.get(1).getNom());
        verify(produitRepository).findAll();
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        when(produitRepository.findAll()).thenReturn(List.of());

        List<ProduitResponseDTO> result = produitService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenExists_ShouldReturnProduct() {
        Produit p = createProduit(1, "Test", 10, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));

        ProduitResponseDTO result = produitService.findById(1);

        assertEquals("Test", result.getNom());
        assertEquals(10, result.getQuantite());
        assertEquals(new BigDecimal("5.00"), result.getPrixUnitaire());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProduitNonTrouveException.class, () -> produitService.findById(999));
    }

    @Test
    void rechercher_WithTerm_ShouldReturnMatchingProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Produit p = createProduit(1, "Produit Test", 10, new BigDecimal("5.00"), "Desc");
        Page<Produit> page = new PageImpl<>(List.of(p));
        when(produitRepository.findByNomContainingIgnoreCase("Test", pageable)).thenReturn(page);

        Page<ProduitResponseDTO> result = produitService.rechercher("Test", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Produit Test", result.getContent().get(0).getNom());
    }

    @Test
    void rechercher_WithNullTerm_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("5.00"), "Desc");
        Page<Produit> page = new PageImpl<>(List.of(p));
        when(produitRepository.findByNomContainingIgnoreCase("", pageable)).thenReturn(page);

        Page<ProduitResponseDTO> result = produitService.rechercher(null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void creer_ShouldCreateAndReturnProduct() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Nouveau");
        dto.setQuantite(5);
        dto.setPrixUnitaire(new BigDecimal("15.00"));
        dto.setDescription("Description");

        Produit saved = createProduit(1, "Nouveau", 5, new BigDecimal("15.00"), "Description");
        when(produitRepository.save(any(Produit.class))).thenReturn(saved);

        ProduitResponseDTO result = produitService.creer(dto);

        assertEquals(1, result.getId());
        assertEquals("Nouveau", result.getNom());
        assertEquals(5, result.getQuantite());
        assertEquals(new BigDecimal("15.00"), result.getPrixUnitaire());
        assertEquals("Description", result.getDescription());
    }

    @Test
    void modifier_WhenExists_ShouldUpdateAndReturn() {
        Produit existing = createProduit(1, "Ancien", 10, new BigDecimal("10.00"), "Ancienne desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(existing));

        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Modifié");
        dto.setQuantite(20);
        dto.setPrixUnitaire(new BigDecimal("25.00"));
        dto.setDescription("Nouvelle description");

        when(produitRepository.save(any(Produit.class))).thenReturn(existing);

        ProduitResponseDTO result = produitService.modifier(1, dto);

        assertEquals("Modifié", result.getNom());
        assertEquals(20, result.getQuantite());
        assertEquals(new BigDecimal("25.00"), result.getPrixUnitaire());
        assertEquals("Nouvelle description", result.getDescription());
    }

    @Test
    void modifier_WhenNotExists_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(1);
        dto.setPrixUnitaire(new BigDecimal("1.00"));

        assertThrows(ProduitNonTrouveException.class, () -> produitService.modifier(999, dto));
    }

    @Test
    void supprimer_WhenExists_ShouldDelete() {
        Produit p = createProduit(1, "Test", 10, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));
        doNothing().when(produitRepository).delete(p);
        doNothing().when(produitRepository).flush();

        produitService.supprimer(1);

        verify(produitRepository).delete(p);
        verify(produitRepository).flush();
    }

    @Test
    void supprimer_WhenNotExists_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProduitNonTrouveException.class, () -> produitService.supprimer(999));
    }

    @Test
    void supprimer_WhenReferencedByCommande_ShouldThrowProduitReferenceParCommandeException() {
        Produit p = createProduit(1, "Test", 10, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));
        doThrow(DataIntegrityViolationException.class).when(produitRepository).delete(p);

        assertThrows(ProduitReferenceParCommandeException.class, () -> produitService.supprimer(1));
    }

    @Test
    void decrementerStock_WhenSufficientStock_ShouldDecrease() {
        Produit p = createProduit(1, "Test", 10, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));
        when(produitRepository.save(any(Produit.class))).thenReturn(p);

        produitService.decrementerStock(1, 3);

        assertEquals(7, p.getQuantite());
        verify(produitRepository).save(p);
    }

    @Test
    void decrementerStock_WhenInsufficientStock_ShouldThrowException() {
        Produit p = createProduit(1, "Test", 2, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));

        assertThrows(StockInsuffisantException.class, () -> produitService.decrementerStock(1, 5));
        assertEquals(2, p.getQuantite());
    }

    @Test
    void decrementerStock_WhenExactStock_ShouldAllow() {
        Produit p = createProduit(1, "Test", 5, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));
        when(produitRepository.save(any(Produit.class))).thenReturn(p);

        produitService.decrementerStock(1, 5);

        assertEquals(0, p.getQuantite());
    }

    @Test
    void decrementerStock_WhenProductNotFound_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProduitNonTrouveException.class, () -> produitService.decrementerStock(999, 1));
    }

    @Test
    void incrementerStock_ShouldIncrease() {
        Produit p = createProduit(1, "Test", 10, new BigDecimal("5.00"), "Desc");
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));
        when(produitRepository.save(any(Produit.class))).thenReturn(p);

        produitService.incrementerStock(1, 5);

        assertEquals(15, p.getQuantite());
        verify(produitRepository).save(p);
    }

    @Test
    void incrementerStock_WhenProductNotFound_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProduitNonTrouveException.class, () -> produitService.incrementerStock(999, 5));
    }

    @Test
    void getDashboard_WithDefaultThreshold_ShouldCalculateCorrectly() {
        Produit p1 = createProduit(1, "P1", 10, new BigDecimal("10.00"), null);
        Produit p2 = createProduit(2, "P2", 3, new BigDecimal("20.00"), null);
        Produit p3 = createProduit(3, "P3", 0, new BigDecimal("5.00"), null);
        when(produitRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        DashboardDTO result = produitService.getDashboard(null);

        assertEquals(13, result.getQuantiteTotale());
        assertEquals(new BigDecimal("160.00"), result.getValeurTotaleStock());
        assertEquals(2, result.getNombreProduitsEnRupture());
        assertEquals(2, result.getProduitsEnRupture().size());
    }

    @Test
    void getDashboard_WithCustomThreshold_ShouldFilterCorrectly() {
        Produit p1 = createProduit(1, "P1", 10, new BigDecimal("10.00"), null);
        Produit p2 = createProduit(2, "P2", 8, new BigDecimal("20.00"), null);
        when(produitRepository.findAll()).thenReturn(List.of(p1, p2));

        DashboardDTO result = produitService.getDashboard(7);

        assertEquals(18, result.getQuantiteTotale());
        assertEquals(new BigDecimal("260.00"), result.getValeurTotaleStock());
        assertEquals(0, result.getNombreProduitsEnRupture());
        assertTrue(result.getProduitsEnRupture().isEmpty());
    }

    @Test
    void getDashboard_WhenNoProducts_ShouldReturnZeros() {
        when(produitRepository.findAll()).thenReturn(List.of());

        DashboardDTO result = produitService.getDashboard(null);

        assertEquals(0, result.getQuantiteTotale());
        assertEquals(BigDecimal.ZERO, result.getValeurTotaleStock());
        assertEquals(0, result.getNombreProduitsEnRupture());
        assertTrue(result.getProduitsEnRupture().isEmpty());
    }
}
