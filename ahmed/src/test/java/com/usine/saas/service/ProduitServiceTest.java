package com.usine.saas.service;

import com.usine.saas.dto.ProduitRequestDTO;
import com.usine.saas.entity.Produit;
import com.usine.saas.entity.Usine;
import com.usine.saas.exception.AccesRefuseException;
import com.usine.saas.exception.ProduitNonTrouveException;
import com.usine.saas.exception.StockInsuffisantException;
import com.usine.saas.repository.ProduitRepository;
import com.usine.saas.repository.UsineRepository;
import com.usine.saas.security.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private UsineRepository usineRepository;
    @Mock
    private HistoriqueService historiqueService;
    @Mock
    private UserContext ctx;

    @InjectMocks
    private ProduitService produitService;

    private Usine usine;
    private Produit produit;

    @BeforeEach
    void setUp() {
        usine = new Usine();
        usine.setId(1);

        produit = new Produit();
        produit.setId(10);
        produit.setUsine(usine);
        produit.setNom("Vis M6");
        produit.setQuantite(20);
        produit.setPrixUnitaire(new BigDecimal("2.50"));
    }

    @Test
    void creer_avecDroit_doitReussir() {
        when(ctx.isAdmin()).thenReturn(false);
        when(ctx.peutAjouterProduit()).thenReturn(true);
        when(ctx.getUsineId()).thenReturn(1);
        when(ctx.getUserId()).thenReturn(5);
        when(usineRepository.getReferenceById(1)).thenReturn(usine);
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Boulon M8");
        dto.setQuantite(100);
        dto.setPrixUnitaire(new BigDecimal("1.20"));

        var result = produitService.creer(ctx, dto);

        assertThat(result.getNom()).isEqualTo("Boulon M8");
        verify(historiqueService).log(eq(1), eq(5), anyString(), eq("STOCK"));
    }

    @Test
    void creer_sansDroit_doitLeverAccesRefuse() {
        when(ctx.isAdmin()).thenReturn(false);
        when(ctx.peutAjouterProduit()).thenReturn(false);

        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Boulon M8");
        dto.setQuantite(10);
        dto.setPrixUnitaire(BigDecimal.TEN);

        assertThatThrownBy(() -> produitService.creer(ctx, dto))
                .isInstanceOf(AccesRefuseException.class);

        verifyNoInteractions(historiqueService);
    }

    @Test
    void decrementerStock_quantiteSuffisante_doitReussir() {
        when(produitRepository.findById(10)).thenReturn(Optional.of(produit));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        produitService.decrementerStock(10, 5);

        assertThat(produit.getQuantite()).isEqualTo(15);
        verify(historiqueService).logStock(1, null, 10, 20, 15);
    }

    @Test
    void decrementerStock_stockInsuffisant_doitLeverException() {
        when(produitRepository.findById(10)).thenReturn(Optional.of(produit));

        assertThatThrownBy(() -> produitService.decrementerStock(10, 50))
                .isInstanceOf(StockInsuffisantException.class);

        // La quantité ne doit pas avoir changé
        assertThat(produit.getQuantite()).isEqualTo(20);
        verify(produitRepository, never()).save(any());
    }

    @Test
    void decrementerStock_produitInexistant_doitLeverException() {
        when(produitRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.decrementerStock(999, 1))
                .isInstanceOf(ProduitNonTrouveException.class);
    }

    @Test
    void getProduitDuneAutreUsine_doitEtreInvisible() {
        Usine autreUsine = new Usine();
        autreUsine.setId(2);
        produit.setUsine(autreUsine);

        when(ctx.getUsineId()).thenReturn(1);
        when(produitRepository.findById(10)).thenReturn(Optional.of(produit));

        assertThatThrownBy(() -> produitService.findById(ctx, 10))
                .isInstanceOf(ProduitNonTrouveException.class);
    }
}
