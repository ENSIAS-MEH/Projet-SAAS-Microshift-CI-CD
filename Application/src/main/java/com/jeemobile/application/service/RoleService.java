package com.jeemobile.application.service;

import java.util.Optional;

import com.jeemobile.application.entity.Roles;

public interface RoleService {

    Optional<Roles> trouverParUserId(Integer userId);

    Roles mettreAJourPermissions(Integer userId,
                                  boolean peutAjouterCommande,
                                  boolean peutModifierCommande,
                                  boolean peutAnnulerCommande,
                                  boolean peutAjouterProduit,
                                  boolean peutModifierProduit,
                                  boolean peutSupprimerProduit,
                                  boolean estAdmin);

    Roles accorderAdmin(Integer userId);

    Roles retirerAdmin(Integer userId);
}