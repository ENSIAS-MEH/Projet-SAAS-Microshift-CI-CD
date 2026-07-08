package com.usine.saas.exception;

public class QuantiteInvalideException extends RuntimeException {
    public QuantiteInvalideException() {
        super("La quantité d'un produit ne peut jamais être négative");
    }
}
