package com.jeemobile.application;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestApiIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private static Integer sharedProductId;
    private static Integer sharedOrderId;

    @Test
    @Order(1)
    void createProduct_ShouldSucceed() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("API Test Product");
        dto.setQuantite(100);
        dto.setPrixUnitaire(new BigDecimal("29.99"));
        dto.setDescription("Created via REST API");

        ResponseEntity<ProduitResponseDTO> response = rest.postForEntity(
                "/api/produits", dto, ProduitResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProduitResponseDTO body = response.getBody();
        assertNotNull(body.getId());
        assertEquals("API Test Product", body.getNom());
        assertEquals(100, body.getQuantite());
        assertEquals(new BigDecimal("29.99"), body.getPrixUnitaire());
        sharedProductId = body.getId();
    }

    @Test
    @Order(2)
    void createProduct_WithInvalidData_ShouldReturn400() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("");
        dto.setQuantite(-1);
        dto.setPrixUnitaire(null);

        ResponseEntity<Map> response = rest.postForEntity(
                "/api/produits", dto, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void getProduct_ShouldReturnProduct() {
        ResponseEntity<ProduitResponseDTO> response = rest.getForEntity(
                "/api/produits/{id}", ProduitResponseDTO.class, sharedProductId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("API Test Product", response.getBody().getNom());
    }

    @Test
    @Order(4)
    void getProduct_WithInvalidId_ShouldReturn404() {
        ResponseEntity<Map> response = rest.getForEntity(
                "/api/produits/{id}", Map.class, 9999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(5)
    void updateProduct_ShouldSucceed() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Updated Product");
        dto.setQuantite(50);
        dto.setPrixUnitaire(new BigDecimal("19.99"));

        ResponseEntity<ProduitResponseDTO> response = rest.exchange(
                "/api/produits/{id}", HttpMethod.PUT,
                new HttpEntity<>(dto), ProduitResponseDTO.class, sharedProductId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product", response.getBody().getNom());
        assertEquals(50, response.getBody().getQuantite());
    }

    @Test
    @Order(6)
    void createOrder_ShouldSucceed() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(sharedProductId);
        dto.setQuantite(3);

        ResponseEntity<CommandeResponseDTO> response = rest.postForEntity(
                "/api/commandes", dto, CommandeResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CommandeResponseDTO body = response.getBody();
        assertNotNull(body.getId());
        assertEquals(sharedProductId, body.getProduitId());
        assertEquals(3, body.getQuantite());
        assertEquals(EtatCommande.EN_ATTENTE, body.getEtat());
        assertEquals(new BigDecimal("59.97"), body.getPrixTotal());
        sharedOrderId = body.getId();
    }

    @Test
    @Order(7)
    void createOrder_WithInvalidProduct_ShouldReturn404() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(9999);
        dto.setQuantite(1);

        ResponseEntity<Map> response = rest.postForEntity(
                "/api/commandes", dto, Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    void updateOrderState_ShouldSucceed() {
        ResponseEntity<CommandeResponseDTO> response = rest.exchange(
                "/api/commandes/{id}/etat", HttpMethod.PUT,
                new HttpEntity<>(EtatCommande.VALIDEE),
                CommandeResponseDTO.class, sharedOrderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EtatCommande.VALIDEE, response.getBody().getEtat());
    }

    @Test
    @Order(9)
    void getAllProducts_ShouldReturnList() {
        ResponseEntity<ProduitResponseDTO[]> response = rest.getForEntity(
                "/api/produits", ProduitResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProduitResponseDTO> products = Arrays.asList(response.getBody());
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(sharedProductId)));
    }

    @Test
    @Order(10)
    void getDashboard_ShouldReturnStats() {
        ResponseEntity<Map> response = rest.getForEntity(
                "/api/produits/dashboard", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body.get("quantiteTotale"));
        assertNotNull(body.get("valeurTotaleStock"));
        assertNotNull(body.get("nombreProduitsEnRupture"));
        assertNotNull(body.get("produitsEnRupture"));
    }

    @Test
    @Order(11)
    void decrementStock_ShouldSucceed() {
        ResponseEntity<Void> response = rest.exchange(
                "/api/produits/{id}/decrementer?quantite=5", HttpMethod.POST,
                null, Void.class, sharedProductId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(12)
    void incrementStock_ShouldSucceed() {
        ResponseEntity<Void> response = rest.exchange(
                "/api/produits/{id}/incrementer?quantite=10", HttpMethod.POST,
                null, Void.class, sharedProductId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(13)
    void deleteOrder_ShouldSucceed() {
        rest.delete("/api/commandes/{id}", sharedOrderId);

        ResponseEntity<Map> response = rest.getForEntity(
                "/api/commandes/{id}", Map.class, sharedOrderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(14)
    void deleteProduct_ShouldSucceed() {
        rest.delete("/api/produits/{id}", sharedProductId);

        ResponseEntity<Map> response = rest.getForEntity(
                "/api/produits/{id}", Map.class, sharedProductId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(15)
    void deleteProduct_WithInvalidId_ShouldReturn404() {
        ResponseEntity<Map> response = rest.exchange(
                "/api/produits/{id}", HttpMethod.DELETE,
                null, Map.class, 9999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
