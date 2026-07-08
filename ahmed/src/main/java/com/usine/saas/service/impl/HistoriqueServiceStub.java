package com.usine.saas.service.impl;

import com.usine.saas.service.HistoriqueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implémentation temporaire de HistoriqueService : se contente de logger dans la
 * console au lieu d'écrire dans les tables historique / historique_stockage.
 * Actif uniquement avec le profil Spring "dev-standalone".
 *
 * A SUPPRIMER dès que le membre 3 fournit la vraie implémentation persistant
 * dans la base (tables historique / historique_stockage du schema.sql).
 */
@Service
@Profile("dev-standalone")
public class HistoriqueServiceStub implements HistoriqueService {

    private static final Logger log = LoggerFactory.getLogger(HistoriqueServiceStub.class);

    @Override
    public void logStock(Integer usineId, Integer userId, Integer produitId, Integer quantiteAvant, Integer quantiteApres) {
        log.info("[HISTORIQUE-STOCK][usine={}] user={} produit={} : {} -> {}",
                usineId, userId, produitId, quantiteAvant, quantiteApres);
    }

    @Override
    public void log(Integer usineId, Integer userId, String description, String typeAction) {
        log.info("[HISTORIQUE][usine={}] user={} [{}] {}", usineId, userId, typeAction, description);
    }
}
