package com.katalyst.gestionstock.service;

import com.katalyst.gestionstock.dto.MouvementLotItem;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.entity.TypeMouvement;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.StockInsuffisantException;
import com.katalyst.gestionstock.repository.MouvementStockRepository;
import com.katalyst.gestionstock.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;

    public MouvementStockService(MouvementStockRepository mouvementStockRepository,
                                  ProduitRepository produitRepository) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.produitRepository = produitRepository;
    }

    @Transactional
    public MouvementStock enregistrerMouvement(String reference, TypeMouvement type,
                                                Integer quantite, String motif) {
        if (type == TypeMouvement.ENTREE) {
            int lignesMisesAJour = produitRepository.incrementerQuantite(reference, quantite);
            if (lignesMisesAJour == 0) {
                throw new ProduitNotFoundException(reference);
            }
        } else {
            int lignesMisesAJour = produitRepository.decrementerQuantiteSiSuffisant(reference, quantite);
            if (lignesMisesAJour == 0) {
                Produit produit = produitRepository.findByReference(reference)
                        .orElseThrow(() -> new ProduitNotFoundException(reference));
                throw new StockInsuffisantException(reference, produit.getQuantite(), quantite);
            }
        }

        Produit produitMisAJour = produitRepository.findByReference(reference)
                .orElseThrow(() -> new ProduitNotFoundException(reference));

        MouvementStock mouvement = new MouvementStock(produitMisAJour, type, quantite, motif);
        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public List<MouvementStock> enregistrerMouvementsEnLot(List<MouvementLotItem> mouvements) {
        List<MouvementStock> resultats = new ArrayList<>();
        for (MouvementLotItem item : mouvements) {
            resultats.add(enregistrerMouvement(item.reference(), item.type(), item.quantite(), item.motif()));
        }
        return resultats;
    }
}
