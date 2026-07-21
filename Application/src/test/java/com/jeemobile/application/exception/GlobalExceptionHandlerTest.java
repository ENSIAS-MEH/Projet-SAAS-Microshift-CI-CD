package com.jeemobile.application.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleProduitNonTrouve_ShouldReturn404() {
        ProduitNonTrouveException ex = new ProduitNonTrouveException(1);

        ResponseEntity<Object> response = handler.handleNonTrouve(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("Produit introuvable (id=1)", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleCommandeNonTrouve_ShouldReturn404() {
        CommandeNonTrouveException ex = new CommandeNonTrouveException(5);

        ResponseEntity<Object> response = handler.handleCommandeNonTrouve(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Commande introuvable (id=5)", body.get("message"));
    }

    @Test
    void handleStockInsuffisant_ShouldReturn409() {
        StockInsuffisantException ex = new StockInsuffisantException(1, 5, 10);

        ResponseEntity<Object> response = handler.handleStockInsuffisant(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(((String) body.get("message")).contains("Stock insuffisant"));
    }

    @Test
    void handleAccesRefuse_ShouldReturn403() {
        AccesRefuseException ex = new AccesRefuseException("supprimer");

        ResponseEntity<Object> response = handler.handleAccesRefuse(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(((String) body.get("message")).contains("Accès refusé"));
    }

    @Test
    void handleQuantiteInvalide_ShouldReturn400() {
        QuantiteInvalideException ex = new QuantiteInvalideException();

        ResponseEntity<Object> response = handler.handleQuantiteInvalide(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("La quantité d'un produit ne peut jamais être négative", body.get("message"));
    }

    @Test
    void handleReference_ShouldReturn409() {
        ProduitReferenceParCommandeException ex = new ProduitReferenceParCommandeException(1);

        ResponseEntity<Object> response = handler.handleReference(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(((String) body.get("message")).contains("Impossible de supprimer le produit 1"));
    }

    @Test
    void handleValidation_ShouldReturn400WithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "nom", "Le nom est obligatoire"));
        bindingResult.addError(new FieldError("object", "quantite", "La quantité ne peut pas être négative"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertNotNull(body.get("timestamp"));

        Map<String, String> erreurs = (Map<String, String>) body.get("erreurs");
        assertEquals("Le nom est obligatoire", erreurs.get("nom"));
        assertEquals("La quantité ne peut pas être négative", erreurs.get("quantite"));
    }

    @Test
    void handleValidation_WhenNoErrors_ShouldReturnEmptyErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        Map<String, String> erreurs = (Map<String, String>) body.get("erreurs");
        assertTrue(erreurs.isEmpty());
    }
}
