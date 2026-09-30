package com.katalyst.gestionstock.controller;

import com.katalyst.gestionstock.dto.MouvementCreateRequest;
import com.katalyst.gestionstock.dto.MouvementResponse;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.service.MouvementStockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produits/{reference}/mouvements")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    public MouvementStockController(MouvementStockService mouvementStockService) {
        this.mouvementStockService = mouvementStockService;
    }

    @PostMapping
    public ResponseEntity<MouvementResponse> enregistrerMouvement(
            @PathVariable String reference,
            @Valid @RequestBody MouvementCreateRequest request) {
        MouvementStock mouvement = mouvementStockService.enregistrerMouvement(
                reference,
                request.type(),
                request.quantite(),
                request.motif()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MouvementResponse.from(mouvement));
    }
}
