package com.katalyst.gestionstock.dto;

import com.katalyst.gestionstock.entity.TypeMouvement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MouvementCreateRequest(
        @NotNull(message = "Le type de mouvement est obligatoire") TypeMouvement type,
        @NotNull(message = "La quantite est obligatoire")
        @Positive(message = "La quantite doit etre strictement positive") Integer quantite,
        @NotBlank(message = "Le motif est obligatoire") String motif
) {
}
