package com.jeemobile.application.controller;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.exception.CommandeNonTrouveException;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.service.CommandeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(CommandeRestController.class)
class CommandeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommandeService commandeService;

    private CommandeResponseDTO createDTO(Integer id, String produitNom, Integer quantite,
                                           BigDecimal prixTotal, EtatCommande etat) {
        return new CommandeResponseDTO.Builder()
                .id(id)
                .produitId(1)
                .produitNom(produitNom)
                .quantite(quantite)
                .prixTotal(prixTotal)
                .prixUnitaire(new BigDecimal("10.00"))
                .etat(etat)
                .dateCreation(LocalDateTime.now())
                .dateMaj(LocalDateTime.now())
                .build();
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        when(commandeService.findAll()).thenReturn(List.of(
                createDTO(1, "P1", 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE),
                createDTO(2, "P2", 3, new BigDecimal("30.00"), EtatCommande.VALIDEE)
        ));

        mockMvc.perform(get("/api/commandes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].produitNom").value("P1"))
                .andExpect(jsonPath("$[1].produitNom").value("P2"));
    }

    @Test
    void getAll_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(commandeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/commandes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_WhenExists_ShouldReturnOrder() throws Exception {
        when(commandeService.findById(1)).thenReturn(
                createDTO(1, "Produit", 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE));

        mockMvc.perform(get("/api/commandes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.etat").value("EN_ATTENTE"));
    }

    @Test
    void getById_WhenNotExists_ShouldReturn404() throws Exception {
        when(commandeService.findById(999)).thenThrow(new CommandeNonTrouveException(999));

        mockMvc.perform(get("/api/commandes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_ShouldReturn201() throws Exception {
        CommandeResponseDTO created = createDTO(1, "Produit", 3,
                new BigDecimal("30.00"), EtatCommande.EN_ATTENTE);
        when(commandeService.creer(any(CommandeRequestDTO.class))).thenReturn(created);

        String json = """
                {
                    "produitId": 1,
                    "quantite": 3
                }
                """;

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.etat").value("EN_ATTENTE"));
    }

    @Test
    void create_WithInvalidData_ShouldReturn400() throws Exception {
        String json = """
                {
                    "produitId": null,
                    "quantite": 0
                }
                """;

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(commandeService.creer(any(CommandeRequestDTO.class)))
                .thenThrow(new ProduitNonTrouveException(999));

        String json = """
                {
                    "produitId": 999,
                    "quantite": 1
                }
                """;

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void changerEtat_ShouldReturnUpdatedOrder() throws Exception {
        CommandeResponseDTO updated = createDTO(1, "Produit", 2,
                new BigDecimal("20.00"), EtatCommande.VALIDEE);
        when(commandeService.modifierEtat(1, EtatCommande.VALIDEE)).thenReturn(updated);

        String json = "\"VALIDEE\"";

        mockMvc.perform(put("/api/commandes/1/etat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etat").value("VALIDEE"));
    }

    @Test
    void changerEtat_WhenOrderNotFound_ShouldReturn404() throws Exception {
        when(commandeService.modifierEtat(999, EtatCommande.VALIDEE))
                .thenThrow(new CommandeNonTrouveException(999));

        String json = "\"VALIDEE\"";

        mockMvc.perform(put("/api/commandes/999/etat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/commandes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new CommandeNonTrouveException(999)).when(commandeService).supprimer(999);

        mockMvc.perform(delete("/api/commandes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByProduit_ShouldReturnOrders() throws Exception {
        when(commandeService.findByProduitId(1)).thenReturn(List.of(
                createDTO(1, "Produit", 2, new BigDecimal("20.00"), EtatCommande.EN_ATTENTE)
        ));

        mockMvc.perform(get("/api/commandes/produit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].produitId").value(1));
    }
}
