package com.usine.saas.security;

/**
 * CONTRAT D'INTERFACE avec le module Auth/Users (membre 3).
 *
 * Ce module (Produit/Stock) a besoin de connaître l'utilisateur connecté,
 * son usine, et ses permissions à chaque requête. En attendant l'implémentation
 * réelle (Spring Security + JWT/session), une implémentation "stub" est fournie
 * dans security.impl.UserContextStub pour pouvoir développer et tester ce module
 * de façon autonome.
 *
 * IMPORTANT : convenez avec le membre 3 du nom exact de cette interface et de ses
 * méthodes AVANT de coder, comme demandé dans le cahier des charges. Idéalement,
 * il implémente cette interface (ou on adapte son bean existant pour qu'il l'implémente),
 * en s'appuyant sur les colonnes de la table `roles` et `user.usine_id`.
 */
public interface UserContext {

    Integer getUserId();

    Integer getUsineId();

    boolean isAdmin();

    boolean peutAjouterProduit();

    boolean peutModifierProduit();

    boolean peutSupprimerProduit();
}
