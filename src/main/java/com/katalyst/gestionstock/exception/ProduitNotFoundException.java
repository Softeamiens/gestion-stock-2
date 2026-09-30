package com.katalyst.gestionstock.exception;

public class ProduitNotFoundException extends RuntimeException {

    public ProduitNotFoundException(String reference) {
        super("Aucun produit trouve pour la reference : " + reference);
    }
}
