package com.jeemobile.application.exception;

public class CommandeNonTrouveException extends RuntimeException {
    public CommandeNonTrouveException(Integer id) {
        super("Commande introuvable (id=" + id + ")");
    }
}
