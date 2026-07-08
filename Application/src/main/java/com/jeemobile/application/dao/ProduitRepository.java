package com.jeemobile.application.dao;

import com.jeemobile.application.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    List<Produit> findByNomContainingIgnoreCase(String nom);

    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    List<Produit> findByQuantiteLessThanEqual(Integer seuil);
}
