package com.jeemobile.application.service;

import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.DashboardDTO;
import com.jeemobile.application.dto.ProduitRequestDTO;
import com.jeemobile.application.dto.ProduitResponseDTO;
import com.jeemobile.application.entity.Produit;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import com.jeemobile.application.exception.ProduitReferenceParCommandeException;
import com.jeemobile.application.exception.StockInsuffisantException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProduitService {

    private static final int SEUIL_RUPTURE_PAR_DEFAUT = 5;

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<ProduitResponseDTO> findAll() {
        return produitRepository.findAll().stream().map(this::toDTO).toList();
    }

    public Page<ProduitResponseDTO> rechercher(String terme, Pageable pageable) {
        String recherche = (terme == null) ? "" : terme;
        return produitRepository.findByNomContainingIgnoreCase(recherche, pageable)
                .map(this::toDTO);
    }

    public ProduitResponseDTO findById(Integer id) {
        return toDTO(getProduitOuException(id));
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

    @Transactional
    public ProduitResponseDTO creer(ProduitRequestDTO dto) {
        Produit p = new Produit();
        p.setNom(dto.getNom());
        p.setQuantite(dto.getQuantite());
        p.setPrixUnitaire(dto.getPrixUnitaire());
        p.setDescription(dto.getDescription());

        return toDTO(produitRepository.save(p));
    }

    @Transactional
    public ProduitResponseDTO modifier(Integer id, ProduitRequestDTO dto) {
        Produit p = getProduitOuException(id);

        p.setNom(dto.getNom());
        p.setQuantite(dto.getQuantite());
        p.setPrixUnitaire(dto.getPrixUnitaire());
        p.setDescription(dto.getDescription());

        return toDTO(produitRepository.save(p));
    }

    @Transactional
    public void supprimer(Integer id) {
        Produit p = getProduitOuException(id);
        try {
            produitRepository.delete(p);
            produitRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ProduitReferenceParCommandeException(id);
        }
    }

    @Transactional
    public void decrementerStock(Integer produitId, Integer quantite) {
        Produit p = produitRepository.findById(produitId)
                .orElseThrow(() -> new ProduitNonTrouveException(produitId));

        if (p.getQuantite() < quantite) {
            throw new StockInsuffisantException(produitId, p.getQuantite(), quantite);
        }

        p.setQuantite(p.getQuantite() - quantite);
        produitRepository.save(p);
    }

    @Transactional
    public void incrementerStock(Integer produitId, Integer quantite) {
        Produit p = produitRepository.findById(produitId)
                .orElseThrow(() -> new ProduitNonTrouveException(produitId));

        p.setQuantite(p.getQuantite() + quantite);
        produitRepository.save(p);
    }

    private Produit getProduitOuException(Integer id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNonTrouveException(id));
    }

    private ProduitResponseDTO toDTO(Produit p) {
        return new ProduitResponseDTO(
                p.getId(), p.getNom(), p.getQuantite(), p.getPrixUnitaire(),
                p.getDescription(), p.getDateCreation(), p.getDateMaj());
    }
}
