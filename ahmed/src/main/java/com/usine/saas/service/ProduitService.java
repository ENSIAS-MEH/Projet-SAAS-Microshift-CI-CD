package com.usine.saas.service;

import com.usine.saas.dto.DashboardDTO;
import com.usine.saas.dto.ProduitRequestDTO;
import com.usine.saas.dto.ProduitResponseDTO;
import com.usine.saas.entity.Produit;
import com.usine.saas.entity.Usine;
import com.usine.saas.exception.*;
import com.usine.saas.repository.ProduitRepository;
import com.usine.saas.repository.UsineRepository;
import com.usine.saas.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private static final int SEUIL_RUPTURE_PAR_DEFAUT = 5;

    private final ProduitRepository produitRepository;
    private final UsineRepository usineRepository;
    private final HistoriqueService historiqueService;

    // ---------- Lecture ----------

    public List<ProduitResponseDTO> findAllByUsine(UserContext ctx) {
        return produitRepository.findByUsineId(ctx.getUsineId())
                .stream().map(this::toDTO).toList();
    }

    public Page<ProduitResponseDTO> rechercher(UserContext ctx, String terme, Pageable pageable) {
        String recherche = (terme == null) ? "" : terme;
        return produitRepository
                .findByUsineIdAndNomContainingIgnoreCase(ctx.getUsineId(), recherche, pageable)
                .map(this::toDTO);
    }

    public ProduitResponseDTO findById(UserContext ctx, Integer id) {
        return toDTO(getProduitOuException(ctx, id));
    }

    public DashboardDTO getDashboard(UserContext ctx, Integer seuilRupture) {
        int seuil = (seuilRupture != null) ? seuilRupture : SEUIL_RUPTURE_PAR_DEFAUT;
        List<Produit> produits = produitRepository.findByUsineId(ctx.getUsineId());

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

    // ---------- Écriture (avec vérification des droits) ----------

    @Transactional
    public ProduitResponseDTO creer(UserContext ctx, ProduitRequestDTO dto) {
        verifierDroit(ctx, ctx.peutAjouterProduit(), "ajouter un produit");

        Usine usine = usineRepository.getReferenceById(ctx.getUsineId());

        Produit p = new Produit();
        p.setUsine(usine);
        p.setNom(dto.getNom());
        p.setQuantite(dto.getQuantite());
        p.setPrixUnitaire(dto.getPrixUnitaire());
        p.setDescription(dto.getDescription());

        Produit saved = produitRepository.save(p);
        historiqueService.log(ctx.getUsineId(), ctx.getUserId(),
                "Ajout du produit '" + saved.getNom() + "'", "STOCK");

        return toDTO(saved);
    }

    @Transactional
    public ProduitResponseDTO modifier(UserContext ctx, Integer id, ProduitRequestDTO dto) {
        verifierDroit(ctx, ctx.peutModifierProduit(), "modifier un produit");

        Produit p = getProduitOuException(ctx, id);
        Integer qteAvant = p.getQuantite();

        p.setNom(dto.getNom());
        p.setQuantite(dto.getQuantite());
        p.setPrixUnitaire(dto.getPrixUnitaire());
        p.setDescription(dto.getDescription());

        Produit saved = produitRepository.save(p);

        if (!qteAvant.equals(saved.getQuantite())) {
            historiqueService.logStock(ctx.getUsineId(), ctx.getUserId(), id, qteAvant, saved.getQuantite());
        }

        return toDTO(saved);
    }

    @Transactional
    public void supprimer(UserContext ctx, Integer id) {
        verifierDroit(ctx, ctx.peutSupprimerProduit(), "supprimer un produit");

        Produit p = getProduitOuException(ctx, id);
        try {
            produitRepository.delete(p);
            produitRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Le produit est référencé par au moins une commande (FK fk_commande_produit).
            // Cf. remarque du cahier des charges : envisager un soft-delete (colonne `actif`
            // à ajouter en concertation avec l'équipe si ce cas devient fréquent).
            throw new ProduitReferenceParCommandeException(id);
        }

        historiqueService.log(ctx.getUsineId(), ctx.getUserId(),
                "Suppression du produit '" + p.getNom() + "'", "STOCK");
    }

    // ---------- Méthodes exposées au module Commandes (membre 2) ----------
    // Contrat : voir README_module_produits.md

    @Transactional
    public void decrementerStock(Integer produitId, Integer quantite) {
        Produit p = produitRepository.findById(produitId)
                .orElseThrow(() -> new ProduitNonTrouveException(produitId));

        if (p.getQuantite() < quantite) {
            throw new StockInsuffisantException(produitId, p.getQuantite(), quantite);
        }

        Integer qteAvant = p.getQuantite();
        p.setQuantite(qteAvant - quantite);
        produitRepository.save(p);

        historiqueService.logStock(p.getUsine().getId(), null, produitId, qteAvant, p.getQuantite());
    }

    @Transactional
    public void incrementerStock(Integer produitId, Integer quantite) {
        // Utile par ex. lors de l'annulation d'une commande déjà validée.
        Produit p = produitRepository.findById(produitId)
                .orElseThrow(() -> new ProduitNonTrouveException(produitId));

        Integer qteAvant = p.getQuantite();
        p.setQuantite(qteAvant + quantite);
        produitRepository.save(p);

        historiqueService.logStock(p.getUsine().getId(), null, produitId, qteAvant, p.getQuantite());
    }

    // ---------- Utilitaires privés ----------

    private Produit getProduitOuException(UserContext ctx, Integer id) {
        Produit p = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNonTrouveException(id));

        // Isolation multi-tenant stricte : un produit d'une autre usine n'existe
        // simplement pas pour cet utilisateur (on ne distingue pas 404 / 403 ici
        // pour ne pas révéler l'existence de données d'un autre tenant).
        if (!p.getUsine().getId().equals(ctx.getUsineId())) {
            throw new ProduitNonTrouveException(id);
        }
        return p;
    }

    private void verifierDroit(UserContext ctx, boolean permissionSpecifique, String action) {
        if (!ctx.isAdmin() && !permissionSpecifique) {
            throw new AccesRefuseException(action);
        }
    }

    private ProduitResponseDTO toDTO(Produit p) {
        return new ProduitResponseDTO(
                p.getId(), p.getNom(), p.getQuantite(), p.getPrixUnitaire(),
                p.getDescription(), p.getDateCreation(), p.getDateMaj());
    }
}
