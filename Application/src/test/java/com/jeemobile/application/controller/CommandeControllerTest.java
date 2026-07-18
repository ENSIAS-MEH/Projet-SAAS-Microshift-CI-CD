package com.jeemobile.application.controller;

import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.service.CommandeService;
import com.jeemobile.application.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommandeControllerTest {

    @Mock
    private CommandeService commandeService;

    @Mock
    private ProduitService produitService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CommandeController controller = new CommandeController(commandeService, produitService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void list_ShouldAddAttributes() throws Exception {
        when(commandeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/commandes"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("commandes"))
                .andExpect(model().attributeExists("etats"))
                .andExpect(model().attributeExists("etatData"))
                .andExpect(model().attributeExists("produitData"))
                .andExpect(view().name("commandes/list"));
    }

    @Test
    void showForm_ShouldAddAttributes() throws Exception {
        when(produitService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/commandes/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("commande"))
                .andExpect(model().attributeExists("produits"))
                .andExpect(view().name("commandes/form"));
    }

    @Test
    void save_WithValidData_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/commandes")
                        .param("produitId", "1")
                        .param("quantite", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/commandes"));

        verify(commandeService).creer(any(CommandeRequestDTO.class));
    }

    @Test
    void save_WithInvalidData_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/commandes")
                        .param("produitId", "")
                        .param("quantite", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changerEtat_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/commandes/1/etat")
                        .param("etat", "VALIDEE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/commandes"));

        verify(commandeService).modifierEtat(1, EtatCommande.VALIDEE);
    }

    @Test
    void delete_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/commandes/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/commandes"));

        verify(commandeService).supprimer(1);
    }
}
