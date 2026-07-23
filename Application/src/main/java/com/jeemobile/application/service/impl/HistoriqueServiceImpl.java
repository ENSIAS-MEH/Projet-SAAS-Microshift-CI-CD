package com.jeemobile.application.service.impl;

import com.jeemobile.application.dao.HistoriqueRepository;
import com.jeemobile.application.dao.UsineRepository;
import com.jeemobile.application.entity.Historique;
import com.jeemobile.application.entity.Usine;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.enums.TypeAction;
import com.jeemobile.application.exception.RessourceIntrouvableException;
import com.jeemobile.application.service.HistoriqueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class HistoriqueServiceImpl implements HistoriqueService {

    private final HistoriqueRepository historiqueRepository;
    private final UsineRepository usineRepository;

    public HistoriqueServiceImpl(HistoriqueRepository historiqueRepository, UsineRepository usineRepository) {
        this.historiqueRepository = historiqueRepository;
        this.usineRepository = usineRepository;
    }

    @Override
    @Transactional
    public void logAction(Integer usineId, Integer userId, String description, TypeAction type) {
        Usine usine = usineRepository.findById(usineId)
                .orElseThrow(() -> new RessourceIntrouvableException("Usine introuvable, id=" + usineId));

        User userRef = null;
        if (userId != null) {
            userRef = new User();
            userRef.setId(userId);
        }

        Historique historique = new Historique(usine, userRef, description, type);
        historiqueRepository.save(historique);
    }

    @Override
    @Transactional
    public void logStock(Integer usineId, Integer produitId, int qteAvant, int qteApres, Integer userId) {
        String description = String.format(
                "Produit id=%d : quantité %d -> %d", produitId, qteAvant, qteApres);
        logAction(usineId, userId, description, TypeAction.STOCK);
    }

    @Override
    @Transactional
    public void logCommande(Integer usineId, Integer commandeId, String etatAvant, String etatApres, Integer userId) {
        String description = String.format(
                "Commande id=%d : état %s -> %s", commandeId, etatAvant, etatApres);
        logAction(usineId, userId, description, TypeAction.COMMANDE);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Historique> listerParUsine(Integer usineId, Pageable pageable) {
        return historiqueRepository.findByUsineIdOrderByTimestampDesc(usineId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Historique> listerParUsineEtType(Integer usineId, TypeAction type, Pageable pageable) {
        return historiqueRepository.findByUsineIdAndTypeActionOrderByTimestampDesc(usineId, type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Historique> listerParUsineEtUtilisateur(Integer usineId, Integer userId, Pageable pageable) {
        return historiqueRepository.findByUsineIdAndUserIdOrderByTimestampDesc(usineId, userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Historique> listerParPeriode(Integer usineId, LocalDateTime debut, LocalDateTime fin, Pageable pageable) {
        return historiqueRepository.findByUsineIdAndTimestampBetweenOrderByTimestampDesc(usineId, debut, fin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Historique> rechercherAvecFiltres(Integer usineId, TypeAction type, LocalDateTime debut,
                                                   LocalDateTime fin, Pageable pageable) {
        if (type != null && debut != null && fin != null) {
            return historiqueRepository.findByUsineIdAndTypeActionAndTimestampBetweenOrderByTimestampDesc(usineId, type, debut, fin, pageable);
        }
        if (type != null) {
            return historiqueRepository.findByUsineIdAndTypeActionOrderByTimestampDesc(usineId, type, pageable);
        }
        if (debut != null && fin != null) {
            return historiqueRepository.findByUsineIdAndTimestampBetweenOrderByTimestampDesc(usineId, debut, fin, pageable);
        }
        return historiqueRepository.findByUsineIdOrderByTimestampDesc(usineId, pageable);
    }
}