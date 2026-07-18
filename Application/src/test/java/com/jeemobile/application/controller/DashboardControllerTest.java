package com.jeemobile.application.controller;

import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.service.CommandeService;
import com.jeemobile.application.service.DashboardService;
import com.jeemobile.application.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private ProduitService produitService;

    @Mock
    private CommandeService commandeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DashboardController controller = new DashboardController(dashboardService, produitService, commandeService);
        ViewResolver viewResolver = new InternalResourceViewResolver("/templates/", ".html");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void dashboard_ShouldAddAllAttributes() throws Exception {
        DashboardDTO dashboardDTO = new DashboardDTO(100L, new BigDecimal("1000.00"), 2, List.of());
        when(dashboardService.getDashboard(null)).thenReturn(dashboardDTO);
        when(dashboardService.getTotalProduits()).thenReturn(5L);
        when(produitService.findAll()).thenReturn(List.of());
        when(commandeService.countTotal()).thenReturn(10L);
        when(commandeService.countByEtat(EtatCommande.EN_ATTENTE)).thenReturn(3L);
        when(commandeService.countByEtat(EtatCommande.VALIDEE)).thenReturn(5L);
        when(commandeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("dashboard"))
                .andExpect(model().attributeExists("totalProduits"))
                .andExpect(model().attributeExists("produits"))
                .andExpect(model().attributeExists("totalCommandes"))
                .andExpect(model().attributeExists("commandesEnAttente"))
                .andExpect(model().attributeExists("commandesValidees"))
                .andExpect(model().attributeExists("commandes"))
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("totalProduits", 5L))
                .andExpect(model().attribute("totalCommandes", 10L))
                .andExpect(model().attribute("commandesEnAttente", 3L))
                .andExpect(model().attribute("commandesValidees", 5L));
    }
}
