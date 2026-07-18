package com.jeemobile.application.dao;

import com.jeemobile.application.entity.Commande;
import com.jeemobile.application.entity.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    List<Commande> findByEtat(EtatCommande etat);

    long countByEtat(EtatCommande etat);

    List<Commande> findByProduitIdOrderByDateCreationDesc(Integer produitId);
}
