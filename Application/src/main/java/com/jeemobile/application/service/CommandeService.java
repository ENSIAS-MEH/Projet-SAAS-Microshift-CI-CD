package com.jeemobile.application.service;

import com.jeemobile.application.entity.Commande;
import com.jeemobile.application.entity.EtatCommande;
import java.util.List;


public interface CommandeService {

 
    List<Commande> getCommandesParUsine(Integer usineId);

 
    Commande getCommandeParId(Integer id);

 
    Commande creerCommande(Integer produitId, Integer quantite, Integer userId);

    Commande changerEtat(Integer commandeId, EtatCommande nouvelEtat);

   
    void annuler(Integer commandeId);
}