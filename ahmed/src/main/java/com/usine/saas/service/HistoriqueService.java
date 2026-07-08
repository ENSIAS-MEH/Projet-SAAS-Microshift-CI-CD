package com.usine.saas.service;

/**
 * CONTRAT D'INTERFACE avec le module Auth/Logs (membre 3), tables
 * `historique` et `historique_stockage` du schema.sql commun.
 *
 * Ce module (Produit/Stock) appelle logStock(...) après CHAQUE modification
 * de quantité (création, modification manuelle, décrément/incrément suite
 * à une commande), et log(...) pour les autres actions (ajout/suppression produit).
 */
public interface HistoriqueService {

    /**
     * Enregistre un mouvement de stock dans historique + historique_stockage.
     */
    void logStock(Integer usineId, Integer userId, Integer produitId, Integer quantiteAvant, Integer quantiteApres);

    /**
     * Enregistre une action générique (ex: ajout/suppression de produit) dans historique.
     * typeAction attendu : 'STOCK' pour ce module (cf. ENUM type_action du schema.sql).
     */
    void log(Integer usineId, Integer userId, String description, String typeAction);
}
