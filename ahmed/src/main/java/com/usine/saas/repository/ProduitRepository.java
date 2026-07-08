package com.usine.saas.repository;

import com.usine.saas.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    // Isolation multi-tenant : TOUJOURS filtrer par usine_id, jamais de requête sans ce filtre.

    List<Produit> findByUsineId(Integer usineId);

    Optional<Produit> findByUsineIdAndNomIgnoreCase(Integer usineId, String nom);

    Page<Produit> findByUsineIdAndNomContainingIgnoreCase(Integer usineId, String nom, Pageable pageable);

    List<Produit> findByUsineIdAndQuantiteLessThanEqual(Integer usineId, Integer seuil);
}
