package com.usine.saas.exception;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(Integer produitId, Integer quantiteDisponible, Integer quantiteDemandee) {
        super("Stock insuffisant pour le produit " + produitId + " : disponible=" + quantiteDisponible
                + ", demandé=" + quantiteDemandee);
    }
}
