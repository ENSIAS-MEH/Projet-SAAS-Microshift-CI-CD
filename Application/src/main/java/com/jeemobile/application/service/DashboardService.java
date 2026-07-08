package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.Produit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private static final int SEUIL_RUPTURE_PAR_DEFAUT = 5;

    private final ProduitRepository produitRepository;

    public DashboardService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public DashboardDTO getDashboard(Integer seuilRupture) {
        int seuil = (seuilRupture != null) ? seuilRupture : SEUIL_RUPTURE_PAR_DEFAUT;
        List<Produit> produits = produitRepository.findAll();

        long quantiteTotale = produits.stream().mapToLong(Produit::getQuantite).sum();

        BigDecimal valeurTotale = produits.stream()
                .map(p -> p.getPrixUnitaire().multiply(BigDecimal.valueOf(p.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ProduitResponseDTO> enRupture = produits.stream()
                .filter(p -> p.getQuantite() <= seuil)
                .map(this::toDTO)
                .toList();

        return new DashboardDTO(quantiteTotale, valeurTotale, enRupture.size(), enRupture);
    }

    public long getTotalProduits() {
        return produitRepository.count();
    }

    private ProduitResponseDTO toDTO(Produit p) {
        return new ProduitResponseDTO(
                p.getId(), p.getNom(), p.getQuantite(), p.getPrixUnitaire(),
                p.getDescription(), p.getDateCreation(), p.getDateMaj());
    }
}
