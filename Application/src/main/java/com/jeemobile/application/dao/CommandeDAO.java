package com.jeemobile.application.dao;

import com.jeemobile.application.entity.Commande;
import com.jeemobile.application.entity.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CommandeDAO extends JpaRepository<Commande, Integer> {

    @Query("SELECT c FROM Commande c WHERE c.usine.id = :usineId ORDER BY c.dateCreation DESC")
    List<Commande> findAllByUsine(@Param("usineId") Integer usineId);

    @Query("SELECT c FROM Commande c WHERE c.usine.id = :usineId AND c.etat = :etat "
            + "ORDER BY c.dateCreation DESC")
    List<Commande> findByEtat(@Param("usineId") Integer usineId, @Param("etat") EtatCommande etat);
}
