package com.jeemobile.application.controller;

import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.exception.ProduitReferenceParCommandeException;
import com.jeemobile.application.exception.StockInsuffisantException;
import com.jeemobile.application.service.ProduitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProduitRestController.class)
class ProduitRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProduitService produitService;

    private ProduitResponseDTO createDTO(Integer id, String nom, Integer quantite, BigDecimal prix) {
        return new ProduitResponseDTO(id, nom, quantite, prix, "Desc",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        when(produitService.findAll()).thenReturn(List.of(
                createDTO(1, "P1", 10, new BigDecimal("10.00")),
                createDTO(2, "P2", 20, new BigDecimal("20.00"))
        ));

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("P1"))
                .andExpect(jsonPath("$[1].nom").value("P2"));
    }

    @Test
    void getAll_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(produitService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_WhenExists_ShouldReturnProduct() throws Exception {
        when(produitService.findById(1)).thenReturn(createDTO(1, "Test", 10, new BigDecimal("5.00")));

        mockMvc.perform(get("/api/produits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Test"));
    }

    @Test
    void getById_WhenNotExists_ShouldReturn404() throws Exception {
        when(produitService.findById(999)).thenThrow(new ProduitNonTrouveException(999));

        mockMvc.perform(get("/api/produits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturn201() throws Exception {
        ProduitResponseDTO created = createDTO(1, "Nouveau", 5, new BigDecimal("15.00"));
        when(produitService.creer(any(ProduitRequestDTO.class))).thenReturn(created);

        String json = """
                {
                    "nom": "Nouveau",
                    "quantite": 5,
                    "prixUnitaire": 15.00,
                    "description": "Desc"
                }
                """;

        mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Nouveau"));
    }

    @Test
    void create_WithInvalidData_ShouldReturn400() throws Exception {
        String json = """
                {
                    "nom": "",
                    "quantite": -1,
                    "prixUnitaire": null
                }
                """;

        mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WhenExists_ShouldReturnUpdated() throws Exception {
        ProduitResponseDTO updated = createDTO(1, "Modifié", 20, new BigDecimal("25.00"));
        when(produitService.modifier(eq(1), any(ProduitRequestDTO.class))).thenReturn(updated);

        String json = """
                {
                    "nom": "Modifié",
                    "quantite": 20,
                    "prixUnitaire": 25.00
                }
                """;

        mockMvc.perform(put("/api/produits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Modifié"));
    }

    @Test
    void update_WhenNotExists_ShouldReturn404() throws Exception {
        when(produitService.modifier(eq(999), any(ProduitRequestDTO.class)))
                .thenThrow(new ProduitNonTrouveException(999));

        String json = """
                {
                    "nom": "Test",
                    "quantite": 1,
                    "prixUnitaire": 1.00
                }
                """;

        mockMvc.perform(put("/api/produits/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/produits/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new ProduitNonTrouveException(999)).when(produitService).supprimer(999);

        mockMvc.perform(delete("/api/produits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_WhenReferenced_ShouldReturn409() throws Exception {
        doThrow(new ProduitReferenceParCommandeException(1)).when(produitService).supprimer(1);

        mockMvc.perform(delete("/api/produits/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void getDashboard_ShouldReturnStats() throws Exception {
        DashboardDTO dto = new DashboardDTO(100L, new BigDecimal("1000.00"), 2, List.of());
        when(produitService.getDashboard(null)).thenReturn(dto);

        mockMvc.perform(get("/api/produits/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteTotale").value(100))
                .andExpect(jsonPath("$.valeurTotaleStock").value(1000.00));
    }

    @Test
    void getDashboard_WithSeuil_ShouldUseCustomThreshold() throws Exception {
        DashboardDTO dto = new DashboardDTO(50L, new BigDecimal("500.00"), 0, List.of());
        when(produitService.getDashboard(3)).thenReturn(dto);

        mockMvc.perform(get("/api/produits/dashboard?seuilRupture=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteTotale").value(50));
    }

    @Test
    void rechercher_ShouldReturnPage() throws Exception {
        Page<ProduitResponseDTO> page = new PageImpl<>(List.of(
                createDTO(1, "Test", 10, new BigDecimal("5.00"))
        ));
        when(produitService.rechercher(eq("Test"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/produits/recherche?nom=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nom").value("Test"));
    }

    @Test
    void decrementerStock_WithSufficientStock_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/produits/1/decrementer?quantite=3"))
                .andExpect(status().isOk());
    }

    @Test
    void decrementerStock_WithInsufficientStock_ShouldReturn409() throws Exception {
        doThrow(new StockInsuffisantException(1, 2, 5))
                .when(produitService).decrementerStock(1, 5);

        mockMvc.perform(post("/api/produits/1/decrementer?quantite=5"))
                .andExpect(status().isConflict());
    }

    @Test
    void decrementerStock_WhenProductNotFound_ShouldReturn404() throws Exception {
        doThrow(new ProduitNonTrouveException(999))
                .when(produitService).decrementerStock(999, 1);

        mockMvc.perform(post("/api/produits/999/decrementer?quantite=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void incrementerStock_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/produits/1/incrementer?quantite=5"))
                .andExpect(status().isOk());
    }

    @Test
    void incrementerStock_WhenProductNotFound_ShouldReturn404() throws Exception {
        doThrow(new ProduitNonTrouveException(999))
                .when(produitService).incrementerStock(999, 5);

        mockMvc.perform(post("/api/produits/999/incrementer?quantite=5"))
                .andExpect(status().isNotFound());
    }
}
