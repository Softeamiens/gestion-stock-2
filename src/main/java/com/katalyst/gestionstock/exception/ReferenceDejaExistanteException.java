package com.katalyst.gestionstock.exception;

public class ReferenceDejaExistanteException extends RuntimeException {

    public ReferenceDejaExistanteException(String reference) {
        super("Un produit existe deja avec la reference : " + reference);
    }
}
