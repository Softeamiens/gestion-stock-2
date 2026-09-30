package com.katalyst.gestionstock.dto;

import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.TypeMouvement;

import java.time.LocalDateTime;

public record MouvementResponse(Long id, TypeMouvement type, Integer quantite, String motif,
                                 LocalDateTime dateMouvement, Integer quantiteApresMouvement) {

    public static MouvementResponse from(MouvementStock mouvement) {
        return new MouvementResponse(
                mouvement.getId(),
                mouvement.getType(),
                mouvement.getQuantite(),
                mouvement.getMotif(),
                mouvement.getDateMouvement(),
                mouvement.getProduit().getQuantite()
        );
    }
}
