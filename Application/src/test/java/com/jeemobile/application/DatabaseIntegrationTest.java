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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private TestRestTemplate rest;

    @Test
    @Transactional
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

        Integer deletedId = created.getId();
        produitService.supprimer(deletedId);
        assertThrows(ProduitNonTrouveException.class, () -> produitService.findById(deletedId));
    }

    @Test
    @Transactional
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

    @Test
    void addAndDeleteProductViaHttp() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Produit HTTP");
        dto.setQuantite(10);
        dto.setPrixUnitaire(new BigDecimal("99.99"));
        dto.setDescription("Description HTTP");

        ResponseEntity<ProduitResponseDTO> createResponse =
                rest.postForEntity("/api/produits", dto, ProduitResponseDTO.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        ProduitResponseDTO created = createResponse.getBody();
        assertNotNull(created.getId());

        ResponseEntity<ProduitResponseDTO> getResponse =
                rest.getForEntity("/api/produits/{id}", ProduitResponseDTO.class, created.getId());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Produit HTTP", getResponse.getBody().getNom());

        rest.delete("/api/produits/{id}", created.getId());

        ResponseEntity<Map> deletedResponse =
                rest.getForEntity("/api/produits/{id}", Map.class, created.getId());
        assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
    }

    @Test
    void addOrderChangeStateAndDeleteViaHttp() {
        ProduitRequestDTO productDto = new ProduitRequestDTO();
        productDto.setNom("Produit commande");
        productDto.setQuantite(50);
        productDto.setPrixUnitaire(new BigDecimal("25.00"));
        ResponseEntity<ProduitResponseDTO> productResponse =
                rest.postForEntity("/api/produits", productDto, ProduitResponseDTO.class);
        Integer productId = productResponse.getBody().getId();

        CommandeRequestDTO orderDto = new CommandeRequestDTO();
        orderDto.setProduitId(productId);
        orderDto.setQuantite(5);
        ResponseEntity<CommandeResponseDTO> orderResponse =
                rest.postForEntity("/api/commandes", orderDto, CommandeResponseDTO.class);
        assertEquals(HttpStatus.CREATED, orderResponse.getStatusCode());
        CommandeResponseDTO order = orderResponse.getBody();
        assertNotNull(order.getId());
        assertEquals(EtatCommande.EN_ATTENTE, order.getEtat());

        ResponseEntity<CommandeResponseDTO> updatedResponse =
                rest.exchange("/api/commandes/{id}/etat", HttpMethod.PUT,
                        new HttpEntity<>(EtatCommande.VALIDEE),
                        CommandeResponseDTO.class, order.getId());
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertEquals(EtatCommande.VALIDEE, updatedResponse.getBody().getEtat());

        rest.delete("/api/commandes/{id}", order.getId());
        rest.delete("/api/produits/{id}", productId);
    }
}
