package com.katalyst.gestionstock.exception;

public class StockInsuffisantException extends RuntimeException {

    public StockInsuffisantException(String reference, Integer quantiteDisponible, Integer quantiteDemandee) {
        super(String.format(
                "Stock insuffisant pour le produit %s : disponible %d, demande %d",
                reference, quantiteDisponible, quantiteDemandee));
    }
}
