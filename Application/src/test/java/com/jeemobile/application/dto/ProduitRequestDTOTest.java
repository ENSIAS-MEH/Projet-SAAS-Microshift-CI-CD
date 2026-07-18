package com.jeemobile.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProduitRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_ShouldPassValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(10);
        dto.setPrixUnitaire(new BigDecimal("15.00"));
        dto.setDescription("Description valide");

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNomBlank_ShouldFailValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("");
        dto.setQuantite(10);
        dto.setPrixUnitaire(new BigDecimal("15.00"));

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Le nom du produit est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void whenQuantiteNull_ShouldFailValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(null);
        dto.setPrixUnitaire(new BigDecimal("15.00"));

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("La quantité est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void whenQuantiteNegative_ShouldFailValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(-5);
        dto.setPrixUnitaire(new BigDecimal("15.00"));

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("La quantité ne peut pas être négative", violations.iterator().next().getMessage());
    }

    @Test
    void whenPrixUnitaireNull_ShouldFailValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(10);
        dto.setPrixUnitaire(null);

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Le prix unitaire est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void whenPrixUnitaireNegative_ShouldFailValidation() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(10);
        dto.setPrixUnitaire(new BigDecimal("-1.00"));

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Le prix unitaire ne peut pas être négatif", violations.iterator().next().getMessage());
    }

    @Test
    void whenMultipleFieldsInvalid_ShouldReportAll() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("");
        dto.setQuantite(null);
        dto.setPrixUnitaire(null);

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(3, violations.size());
    }

    @Test
    void whenDescriptionNull_ShouldPass() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Test");
        dto.setQuantite(5);
        dto.setPrixUnitaire(new BigDecimal("10.00"));

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
