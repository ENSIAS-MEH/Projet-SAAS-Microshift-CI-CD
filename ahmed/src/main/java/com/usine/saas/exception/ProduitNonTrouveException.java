package com.usine.saas.exception;

public class ProduitNonTrouveException extends RuntimeException {
    public ProduitNonTrouveException(Integer id) {
        super("Produit introuvable (id=" + id + ")");
    }
}
