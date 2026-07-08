package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.entity.Produit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id: " + id));
    }

    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    public Produit update(Long id, Produit produit) {
        Produit existing = findById(id);
        existing.setNom(produit.getNom());
        existing.setDescription(produit.getDescription());
        existing.setPrix(produit.getPrix());
        existing.setQuantite(produit.getQuantite());
        return produitRepository.save(existing);
    }

    public void delete(Long id) {
        produitRepository.deleteById(id);
    }

    public List<Produit> searchByNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }
}
