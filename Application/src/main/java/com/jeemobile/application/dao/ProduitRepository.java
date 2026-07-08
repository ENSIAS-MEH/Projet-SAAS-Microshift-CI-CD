package com.jeemobile.application.dao;

import com.jeemobile.application.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    List<Produit> findByNomContainingIgnoreCase(String nom);

    List<Produit> findByPrixBetween(java.math.BigDecimal min, java.math.BigDecimal max);
}
