package com.jeemobile.application.controller;

import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProduitControllerTest {

    @Mock
    private ProduitService produitService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProduitController controller = new ProduitController(produitService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void list_ShouldAddProductsToModel() throws Exception {
        when(produitService.findAll()).thenReturn(List.of(
                new ProduitResponseDTO(1, "P1", 10, new BigDecimal("10.00"), "Desc",
                        LocalDateTime.now(), LocalDateTime.now())
        ));

        mockMvc.perform(get("/produits"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("produits"))
                .andExpect(view().name("produits/list"));
    }

    @Test
    void showForm_ShouldAddEmptyDtoToModel() throws Exception {
        mockMvc.perform(get("/produits/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("produit"))
                .andExpect(view().name("produits/form"));
    }

    @Test
    void save_WithValidData_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/produits")
                        .param("nom", "Test")
                        .param("quantite", "10")
                        .param("prixUnitaire", "15.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produits"));

        verify(produitService).creer(any(ProduitRequestDTO.class));
    }

    @Test
    void save_WithInvalidData_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/produits")
                        .param("nom", "")
                        .param("quantite", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void edit_WhenExists_ShouldShowForm() throws Exception {
        when(produitService.findById(1)).thenReturn(
                new ProduitResponseDTO(1, "P1", 10, new BigDecimal("10.00"), "Desc",
                        LocalDateTime.now(), LocalDateTime.now()));

        mockMvc.perform(get("/produits/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("produit"))
                .andExpect(model().attribute("editId", 1))
                .andExpect(view().name("produits/form"));
    }

    @Test
    void edit_WhenNotExists_ShouldReturnError() {
        when(produitService.findById(999)).thenThrow(new ProduitNonTrouveException(999));

        assertThrows(Exception.class, () -> mockMvc.perform(get("/produits/edit/999")));
    }

    @Test
    void update_WithValidData_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/produits/1")
                        .param("nom", "Updated")
                        .param("quantite", "20")
                        .param("prixUnitaire", "25.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produits"));

        verify(produitService).modifier(eq(1), any(ProduitRequestDTO.class));
    }

    @Test
    void update_WithInvalidData_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/produits/1")
                        .param("nom", "")
                        .param("quantite", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/produits/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produits"));

        verify(produitService).supprimer(1);
    }
}
