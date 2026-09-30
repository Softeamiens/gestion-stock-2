package com.katalyst.gestionstock.dto;

import com.katalyst.gestionstock.entity.Produit;

public record ProduitResponse(String reference, String nom, String description, Integer quantite,
                               String emplacement) {

    public static ProduitResponse from(Produit produit) {
        return new ProduitResponse(
                produit.getReference(),
                produit.getNom(),
                produit.getDescription(),
                produit.getQuantite(),
                produit.getEmplacement()
        );
    }
}
