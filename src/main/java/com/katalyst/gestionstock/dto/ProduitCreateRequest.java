package com.katalyst.gestionstock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProduitCreateRequest(
        @NotBlank(message = "La reference est obligatoire") String reference,
        @NotBlank(message = "Le nom est obligatoire") String nom,
        String description,
        @NotNull(message = "La quantite initiale est obligatoire")
        @Min(value = 0, message = "La quantite initiale ne peut pas etre negative") Integer quantiteInitiale,
        @NotBlank(message = "L'emplacement est obligatoire") String emplacement
) {
}
