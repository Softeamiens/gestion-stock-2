package com.katalyst.gestionstock.controller;

import com.katalyst.gestionstock.dto.ProduitCreateRequest;
import com.katalyst.gestionstock.dto.ProduitResponse;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @PostMapping
    public ResponseEntity<ProduitResponse> creerProduit(@Valid @RequestBody ProduitCreateRequest request) {
        Produit produit = produitService.creerProduit(
                request.reference(),
                request.nom(),
                request.description(),
                request.quantiteInitiale(),
                request.emplacement()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProduitResponse.from(produit));
    }

    @GetMapping
    public ResponseEntity<Page<ProduitResponse>> listerProduits(
            @RequestParam(required = false) String emplacement,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProduitResponse> page = produitService.listerProduits(emplacement, pageable)
                .map(ProduitResponse::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ProduitResponse> obtenirProduit(@PathVariable String reference) {
        Produit produit = produitService.obtenirParReference(reference);
        return ResponseEntity.ok(ProduitResponse.from(produit));
    }

    @GetMapping("/quantite-totale")
    public ResponseEntity<Long> obtenirQuantiteTotale() {
        return ResponseEntity.ok(produitService.compterQuantiteTotale());
    }
}
