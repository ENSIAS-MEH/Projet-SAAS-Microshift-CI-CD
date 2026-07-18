package com.jeemobile.application.service;

import com.jeemobile.application.dao.CommandeRepository;
import com.jeemobile.application.dao.ProduitRepository;
import com.jeemobile.application.dto.CommandeRequestDTO;
import com.jeemobile.application.dto.CommandeResponseDTO;
import com.jeemobile.application.entity.Commande;
import com.jeemobile.application.entity.EtatCommande;
import com.jeemobile.application.entity.Produit;
import com.jeemobile.application.exception.CommandeNonTrouveException;
import com.jeemobile.application.exception.ProduitNonTrouveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;

    public CommandeService(CommandeRepository commandeRepository, ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
    }

    public List<CommandeResponseDTO> findAll() {
        return commandeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CommandeResponseDTO findById(Integer id) {
        return toDTO(getCommandeOuException(id));
    }

    @Transactional
    public CommandeResponseDTO creer(CommandeRequestDTO dto) {
        Produit produit = produitRepository.findById(dto.getProduitId())
                .orElseThrow(() -> new ProduitNonTrouveException(dto.getProduitId()));

        Commande c = new Commande();
        c.setProduit(produit);
        c.setQuantite(dto.getQuantite());
        c.setPrixTotal(produit.getPrixUnitaire().multiply(BigDecimal.valueOf(dto.getQuantite())));

        return toDTO(commandeRepository.save(c));
    }

    @Transactional
    public CommandeResponseDTO modifierEtat(Integer id, EtatCommande nouvelEtat) {
        Commande c = getCommandeOuException(id);

        if (c.getEtat() == EtatCommande.ANNULEE) {
            throw new IllegalStateException("Impossible de modifier une commande annulée");
        }

        if (c.getEtat() == nouvelEtat) {
            return toDTO(c);
        }

        if (nouvelEtat == EtatCommande.VALIDEE && c.getEtat() == EtatCommande.EN_ATTENTE) {
            Produit produit = c.getProduit();
            produit.setQuantite(produit.getQuantite() + c.getQuantite());
            produitRepository.save(produit);
        }

        if (nouvelEtat == EtatCommande.ANNULEE && c.getEtat() == EtatCommande.VALIDEE) {
            Produit produit = c.getProduit();
            produit.setQuantite(produit.getQuantite() - c.getQuantite());
            produitRepository.save(produit);
        }

        c.setEtat(nouvelEtat);
        return toDTO(commandeRepository.save(c));
    }

    @Transactional
    public void supprimer(Integer id) {
        Commande c = getCommandeOuException(id);
        commandeRepository.delete(c);
    }

    public List<CommandeResponseDTO> findByProduitId(Integer produitId) {
        return commandeRepository.findByProduitIdOrderByDateCreationDesc(produitId)
                .stream().map(this::toDTO).toList();
    }

    public long countByEtat(EtatCommande etat) {
        return commandeRepository.countByEtat(etat);
    }

    public long countTotal() {
        return commandeRepository.count();
    }

    private Commande getCommandeOuException(Integer id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeNonTrouveException(id));
    }

    private CommandeResponseDTO toDTO(Commande c) {
        return new CommandeResponseDTO(
                c.getId(),
                c.getProduit().getId(),
                c.getProduit().getNom(),
                c.getQuantite(),
                c.getPrixTotal(),
                c.getProduit().getPrixUnitaire(),
                c.getEtat(),
                c.getDateCreation(),
                c.getDateMaj()
        );
    }
}
