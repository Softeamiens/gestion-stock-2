package com.katalyst.gestionstock.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MouvementLotRequest(
        @NotEmpty(message = "La liste des mouvements ne peut pas etre vide")
        @Valid List<MouvementLotItem> mouvements
) {
}
