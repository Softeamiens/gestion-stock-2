package com.katalyst.gestionstock.service;

import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.ReferenceDejaExistanteException;
import com.katalyst.gestionstock.repository.ProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Transactional
    public Produit creerProduit(String reference, String nom, String description,
                                 Integer quantiteInitiale, String emplacement) {
        if (produitRepository.existsByReference(reference)) {
            throw new ReferenceDejaExistanteException(reference);
        }
        Produit produit = new Produit(reference, nom, description, quantiteInitiale, emplacement);
        return produitRepository.save(produit);
    }

    @Transactional(readOnly = true)
    public Page<Produit> listerProduits(String emplacement, Pageable pageable) {
        if (emplacement != null && !emplacement.isBlank()) {
            return produitRepository.findByEmplacement(emplacement, pageable);
        }
        return produitRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Produit obtenirParReference(String reference) {
        return produitRepository.findByReference(reference)
                .orElseThrow(() -> new ProduitNotFoundException(reference));
    }

    @Transactional(readOnly = true)
    public long compterQuantiteTotale() {
        return produitRepository.sommeQuantites();
    }
}
