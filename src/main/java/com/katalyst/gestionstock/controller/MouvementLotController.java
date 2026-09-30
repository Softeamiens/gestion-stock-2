package com.katalyst.gestionstock.controller;

import com.katalyst.gestionstock.dto.MouvementLotRequest;
import com.katalyst.gestionstock.dto.MouvementResponse;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.service.MouvementStockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements")
public class MouvementLotController {

    private final MouvementStockService mouvementStockService;

    public MouvementLotController(MouvementStockService mouvementStockService) {
        this.mouvementStockService = mouvementStockService;
    }

    @PostMapping("/lot")
    public ResponseEntity<List<MouvementResponse>> enregistrerMouvementsEnLot(
            @Valid @RequestBody MouvementLotRequest request) {
        List<MouvementStock> mouvements = mouvementStockService.enregistrerMouvementsEnLot(request.mouvements());
        List<MouvementResponse> reponses = mouvements.stream().map(MouvementResponse::from).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(reponses);
    }
}
