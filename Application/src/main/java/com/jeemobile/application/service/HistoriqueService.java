package com.jeemobile.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jeemobile.application.entity.Historique;
import com.jeemobile.application.enums.TypeAction;

public interface HistoriqueService {

    void logAction(Integer usineId, Integer userId, String description, TypeAction type);

    void logStock(Integer usineId, Integer produitId, int qteAvant, int qteApres, Integer userId);

    void logCommande(Integer usineId, Integer commandeId, String etatAvant, String etatApres, Integer userId);

    Page<Historique> listerParUsine(Integer usineId, Pageable pageable);

    Page<Historique> listerParUsineEtType(Integer usineId, TypeAction type, Pageable pageable);

    Page<Historique> listerParUsineEtUtilisateur(Integer usineId, Integer userId, Pageable pageable);

    Page<Historique> listerParPeriode(Integer usineId, LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    Page<Historique> rechercherAvecFiltres(Integer usineId, TypeAction type, LocalDateTime debut,
                                            LocalDateTime fin, Pageable pageable);
}