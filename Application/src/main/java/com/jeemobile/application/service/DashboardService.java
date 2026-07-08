package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.PieChartData;
import com.jeemobile.application.entity.Produit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ProduitRepository produitRepository;

    public DashboardService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public PieChartData getRepartitionPrix() {
        Map<String, Long> categories = new LinkedHashMap<>(
                Map.of("Économique (< 50)", 0L, "Standard (50-200)", 0L, "Premium (> 200)", 0L)
        );

        for (Produit p : produitRepository.findAll()) {
            String cat;
            if (p.getPrix().compareTo(new BigDecimal("50")) < 0) {
                cat = "Économique (< 50)";
            } else if (p.getPrix().compareTo(new BigDecimal("200")) <= 0) {
                cat = "Standard (50-200)";
            } else {
                cat = "Premium (> 200)";
            }
            categories.merge(cat, 1L, Long::sum);
        }

        return new PieChartData(
                new ArrayList<>(categories.keySet()),
                new ArrayList<>(categories.values())
        );
    }

    public PieChartData getRepartitionStock() {
        Map<String, Long> categories = new LinkedHashMap<>(
                Map.of("Faible (< 10)", 0L, "Moyen (10-50)", 0L, "Élevé (> 50)", 0L)
        );

        for (Produit p : produitRepository.findAll()) {
            String cat;
            if (p.getQuantite() < 10) {
                cat = "Faible (< 10)";
            } else if (p.getQuantite() <= 50) {
                cat = "Moyen (10-50)";
            } else {
                cat = "Élevé (> 50)";
            }
            categories.merge(cat, 1L, Long::sum);
        }

        return new PieChartData(
                new ArrayList<>(categories.keySet()),
                new ArrayList<>(categories.values())
        );
    }

    public long getTotalProduits() {
        return produitRepository.count();
    }
}
