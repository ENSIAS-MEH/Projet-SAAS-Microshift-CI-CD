package com.jeemobile.application.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeemobile.application.dao.RolesRepository;
import com.jeemobile.application.entity.Roles;
import com.jeemobile.application.exception.RessourceIntrouvableException;
import com.jeemobile.application.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RolesRepository rolesRepository;

    public RoleServiceImpl(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Roles> trouverParUserId(Integer userId) {
        return rolesRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Roles mettreAJourPermissions(Integer userId,
                                         boolean peutAjouterCommande,
                                         boolean peutModifierCommande,
                                         boolean peutAnnulerCommande,
                                         boolean peutAjouterProduit,
                                         boolean peutModifierProduit,
                                         boolean peutSupprimerProduit,
                                         boolean estAdmin) {
        Roles roles = trouverOuLever(userId);
        roles.setPeutAjouterCommande(peutAjouterCommande);
        roles.setPeutModifierCommande(peutModifierCommande);
        roles.setPeutAnnulerCommande(peutAnnulerCommande);
        roles.setPeutAjouterProduit(peutAjouterProduit);
        roles.setPeutModifierProduit(peutModifierProduit);
        roles.setPeutSupprimerProduit(peutSupprimerProduit);
        roles.setEstAdmin(estAdmin);
        return rolesRepository.save(roles);
    }

    @Override
    @Transactional
    public Roles accorderAdmin(Integer userId) {
        Roles roles = trouverOuLever(userId);
        roles.setEstAdmin(true);
        return rolesRepository.save(roles);
    }

    @Override
    @Transactional
    public Roles retirerAdmin(Integer userId) {
        Roles roles = trouverOuLever(userId);
        roles.setEstAdmin(false);
        return rolesRepository.save(roles);
    }

     private Roles trouverOuLever(Integer userId) {
        return rolesRepository.findByUserId(userId)
                .orElseThrow(() -> new RessourceIntrouvableException("Rôles introuvables pour userId=" + userId));
    }
}