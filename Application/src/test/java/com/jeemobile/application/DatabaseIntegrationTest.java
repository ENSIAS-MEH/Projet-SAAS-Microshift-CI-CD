package com.jeemobile.application;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.service.CommandeService;
import com.jeemobile.application.service.ProduitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private CommandeService commandeService;

    @Test
    void addAndDeleteProduct() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Produit Test");
        dto.setQuantite(10);
        dto.setPrixUnitaire(new BigDecimal("99.99"));
        dto.setDescription("Description test");

        ProduitResponseDTO created = produitService.creer(dto);
        assertNotNull(created.getId());
        assertEquals("Produit Test", created.getNom());
        assertEquals(10, created.getQuantite());

        produitService.supprimer(created.getId());
        assertThrows(ProduitNonTrouveException.class,
                () -> produitService.findById(created.getId()));
    }

    @Test
    void addOrderChangeStateAndDelete() {
        ProduitRequestDTO productDto = new ProduitRequestDTO();
        productDto.setNom("Produit pour commande");
        productDto.setQuantite(50);
        productDto.setPrixUnitaire(new BigDecimal("25.00"));
        ProduitResponseDTO product = produitService.creer(productDto);

        CommandeRequestDTO orderDto = new CommandeRequestDTO();
        orderDto.setProduitId(product.getId());
        orderDto.setQuantite(5);
        CommandeResponseDTO order = commandeService.creer(orderDto);

        assertNotNull(order.getId());
        assertEquals(EtatCommande.EN_ATTENTE, order.getEtat());
        assertEquals(product.getId(), order.getProduitId());

        CommandeResponseDTO updated = commandeService.modifierEtat(order.getId(), EtatCommande.VALIDEE);
        assertEquals(EtatCommande.VALIDEE, updated.getEtat());

        commandeService.supprimer(order.getId());
        produitService.supprimer(product.getId());
    }
}
