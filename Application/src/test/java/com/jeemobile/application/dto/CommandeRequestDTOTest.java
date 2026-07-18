package com.jeemobile.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommandeRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_ShouldPass() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(1);
        dto.setQuantite(5);

        Set<ConstraintViolation<CommandeRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenProduitIdNull_ShouldFail() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(null);
        dto.setQuantite(5);

        Set<ConstraintViolation<CommandeRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Le produit est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void whenQuantiteNull_ShouldFail() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(1);
        dto.setQuantite(null);

        Set<ConstraintViolation<CommandeRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("La quantité est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void whenQuantiteZero_ShouldFail() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(1);
        dto.setQuantite(0);

        Set<ConstraintViolation<CommandeRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("La quantité doit être supérieure à zéro", violations.iterator().next().getMessage());
    }

    @Test
    void whenQuantiteNegative_ShouldFail() {
        CommandeRequestDTO dto = new CommandeRequestDTO();
        dto.setProduitId(1);
        dto.setQuantite(-1);

        Set<ConstraintViolation<CommandeRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
