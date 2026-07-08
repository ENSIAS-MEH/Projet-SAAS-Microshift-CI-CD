package com.usine.saas.exception;

public class AccesRefuseException extends RuntimeException {
    public AccesRefuseException(String action) {
        super("Accès refusé : vous n'avez pas la permission de " + action);
    }
}
