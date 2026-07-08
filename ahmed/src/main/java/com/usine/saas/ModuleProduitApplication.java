package com.usine.saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du module Produit/Stock, utilisable seul en développement
 * (avec le profil "dev-standalone" pour activer les stubs UserContext/HistoriqueService).
 *
 * IMPORTANT : quand l'équipe fusionnera les 3 modules dans une seule application
 * Spring Boot, ne garder qu'UNE SEULE classe @SpringBootApplication au total
 * (celle du module Auth, par ex.), et supprimer/adapter celle-ci pour éviter les
 * conflits de contexte Spring.
 */
@SpringBootApplication
public class ModuleProduitApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleProduitApplication.class, args);
    }
}
