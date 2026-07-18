package com.jeemobile.application.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HomeController controller = new HomeController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void home_ShouldRedirectToProduits() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produits"));
    }
}
