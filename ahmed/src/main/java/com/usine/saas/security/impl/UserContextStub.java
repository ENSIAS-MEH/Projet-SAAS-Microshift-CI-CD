package com.usine.saas.security.impl;

import com.usine.saas.security.UserContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implémentation temporaire de UserContext, active uniquement avec le profil
 * Spring "dev-standalone". Simule un administrateur de l'usine 1 avec tous les droits.
 *
 * A SUPPRIMER (ou désactiver via le profil) une fois le module Auth du membre 3
 * intégré et fonctionnel : ce dernier doit fournir un vrai bean UserContext
 * (par ex. basé sur le contexte de sécurité Spring / JWT), qui prendra le relais.
 */
@Component
@Profile("dev-standalone")
public class UserContextStub implements UserContext {

    @Override
    public Integer getUserId() {
        return 1;
    }

    @Override
    public Integer getUsineId() {
        return 1;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public boolean peutAjouterProduit() {
        return true;
    }

    @Override
    public boolean peutModifierProduit() {
        return true;
    }

    @Override
    public boolean peutSupprimerProduit() {
        return true;
    }
}
