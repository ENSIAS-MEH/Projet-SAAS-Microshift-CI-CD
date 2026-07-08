package com.jeemobile.application.exception;

public class ProduitReferenceParCommandeException extends RuntimeException {
    public ProduitReferenceParCommandeException(Integer id) {
        super("Impossible de supprimer le produit " + id
                + " : il est référencé par au moins une commande. Envisagez un soft-delete.");
    }
}
