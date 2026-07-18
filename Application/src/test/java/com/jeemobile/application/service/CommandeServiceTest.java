package com.jeemobile.application.service;

import com.jeemobile.application.dao.CommandeRepository;
import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.entity.Commande;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.entity.Produit;
import com.jeemobile.application.exception.CommandeNonTrouveException;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitRepository produitRepository;

    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        commandeService = new CommandeService(commandeRepository, produitRepository);
    }

    private Produit createProduit(Integer id, String nom, Integer quantite, BigDecimal prix) {
        Produit p = new Produit();
        p.setId(id);
        p.setNom(nom);
        p.setQuantite(quantite);
        p.setPrixUnitaire(prix);
        return p;
    }

    private Commande createCommande(Integer id, Produit produit, Integer quantite, BigDecimal prixTotal, EtatCommande etat) {
        Commande c = new Commande();
        c.setId(id);
        c.setProduit(produit);
        c.setQuantite(quantite);
        c.setPrixTotal(prixTotal);
        c.setEtat(etat);
        c.setDateCreation(LocalDateTime.now());
        c.setDateMaj(LocalDateTime.now());
        return c;
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        List<Commande> commandes = List.of(
                createCommande(1, p, 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE),
                createCommande(2, p, 3, new BigDecimal("30.00"), EtatCommande.VALIDEE)
        );
        when(commandeRepository.findAll()).thenReturn(commandes);

        List<CommandeResponseDTO> result = commandeService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        when(commandeRepository.findAll()).thenReturn(List.of());

        List<CommandeResponseDTO> result = commandeService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenExists_ShouldReturnOrder() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));

        CommandeResponseDTO result = commandeService.findById(1);

        assertEquals(1, result.getId());
        assertEquals(1, result.getProduitId());
        assertEquals("Produit", result.getProduitNom());
        assertEquals(2, result.getQuantite());
        assertEquals(new BigDecimal("20.00"), result.getPrixTotal());
        assertEquals(EtatCommande.EN_ATTENTE, result.getEtat());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(commandeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CommandeNonTrouveException.class, () -> commandeService.findById(999));
    }

    @Test
    void creer_WhenProductExists_ShouldCreateOrder() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        when(produitRepository.findById(1)).thenReturn(Optional.of(p));

        Commande saved = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.save(any(Commande.class))).thenReturn(saved);

        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(1);
        dto.setQuantite(3);

        CommandeResponseDTO result = commandeService.creer(dto);

        assertEquals(1, result.getId());
        assertEquals(1, result.getProduitId());
        assertEquals(3, result.getQuantite());
        assertEquals(new BigDecimal("30.00"), result.getPrixTotal());
        assertEquals(new BigDecimal("10.00"), result.getPrixUnitaire());
    }

    @Test
    void creer_WhenProductNotFound_ShouldThrowException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(999);
        dto.setQuantite(1);

        assertThrows(ProduitNonTrouveException.class, () -> commandeService.creer(dto));
    }

    @Test
    void modifierEtat_FromEnAttenteToValidee_ShouldUpdateStock() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));
        when(produitRepository.save(any(Produit.class))).thenReturn(p);
        when(commandeRepository.save(any(Commande.class))).thenReturn(c);

        CommandeResponseDTO result = commandeService.modifierEtat(1, EtatCommande.VALIDEE);

        assertEquals(EtatCommande.VALIDEE, result.getEtat());
    }

    @Test
    void modifierEtat_FromValideeToAnnulee_ShouldRestoreStock() {
        Produit p = createProduit(1, "Produit", 7, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.VALIDEE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));
        when(produitRepository.save(any(Produit.class))).thenReturn(p);
        when(commandeRepository.save(any(Commande.class))).thenReturn(c);

        CommandeResponseDTO result = commandeService.modifierEtat(1, EtatCommande.ANNULEE);

        assertEquals(EtatCommande.ANNULEE, result.getEtat());
    }

    @Test
    void modifierEtat_WhenAlreadyCancelled_ShouldThrowException() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.ANNULEE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));

        assertThrows(IllegalStateException.class, () -> commandeService.modifierEtat(1, EtatCommande.VALIDEE));
    }

    @Test
    void modifierEtat_WhenSameEtat_ShouldReturnWithoutChanges() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));

        CommandeResponseDTO result = commandeService.modifierEtat(1, EtatCommande.EN_ATTENTE);

        assertEquals(EtatCommande.EN_ATTENTE, result.getEtat());
        verify(commandeRepository, never()).save(any());
    }

    @Test
    void modifierEtat_WhenOrderNotFound_ShouldThrowException() {
        when(commandeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CommandeNonTrouveException.class, () -> commandeService.modifierEtat(999, EtatCommande.VALIDEE));
    }

    @Test
    void modifierEtat_FromEnAttenteToAnnulee_ShouldNotChangeStock() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));
        when(commandeRepository.save(any(Commande.class))).thenReturn(c);

        CommandeResponseDTO result = commandeService.modifierEtat(1, EtatCommande.ANNULEE);

        assertEquals(EtatCommande.ANNULEE, result.getEtat());
        verify(produitRepository, never()).save(any());
    }

    @Test
    void supprimer_WhenExists_ShouldDelete() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        Commande c = createCommande(1, p, 3, new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeRepository.findById(1)).thenReturn(Optional.of(c));

        commandeService.supprimer(1);

        verify(commandeRepository).delete(c);
    }

    @Test
    void supprimer_WhenNotExists_ShouldThrowException() {
        when(commandeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CommandeNonTrouveException.class, () -> commandeService.supprimer(999));
    }

    @Test
    void findByProduitId_ShouldReturnOrdersForProduct() {
        Produit p = createProduit(1, "Produit", 10, new BigDecimal("10.00"));
        List<Commande> commandes = List.of(
                createCommande(1, p, 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE)
        );
        when(commandeRepository.findByProduitIdOrderByDateCreationDesc(1)).thenReturn(commandes);

        List<CommandeResponseDTO> result = commandeService.findByProduitId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getProduitId());
    }

    @Test
    void countByEtat_ShouldReturnCount() {
        when(commandeRepository.countByEtat(EtatCommande.EN_ATTENTE)).thenReturn(3L);

        long result = commandeService.countByEtat(EtatCommande.EN_ATTENTE);

        assertEquals(3, result);
    }

    @Test
    void countTotal_ShouldReturnTotal() {
        when(commandeRepository.count()).thenReturn(10L);

        long result = commandeService.countTotal();

        assertEquals(10, result);
    }
}
