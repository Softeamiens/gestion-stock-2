package com.katalyst.gestionstock.service;

import com.katalyst.gestionstock.dto.MouvementLotItem;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.entity.TypeMouvement;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.StockInsuffisantException;
import com.katalyst.gestionstock.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(MouvementStockService.class)
class MouvementStockServiceIntegrationTest {

    @Autowired
    private MouvementStockService mouvementStockService;

    @Autowired
    private ProduitRepository produitRepository;

    @Test
    void enregistrerMouvement_devraitRetournerLaQuantiteReelleApresMiseAJour_quandLeTypeEstEntree() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));

        MouvementStock mouvement = mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur");

        assertThat(mouvement.getProduit().getQuantite()).isEqualTo(70);
    }

    @Test
    void enregistrerMouvement_devraitRetournerLaQuantiteReelleApresMiseAJour_quandLaSortieEstPossible() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));

        MouvementStock mouvement = mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.SORTIE, 30, "Vente client");

        assertThat(mouvement.getProduit().getQuantite()).isEqualTo(20);
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLeProduitEstIntrouvablePourUneEntree() {
        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "INCONNU", TypeMouvement.ENTREE, 10, "Reception fournisseur"))
                .isInstanceOf(ProduitNotFoundException.class);
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLeProduitEstIntrouvablePourUneSortie() {
        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "INCONNU", TypeMouvement.SORTIE, 10, "Vente client"))
                .isInstanceOf(ProduitNotFoundException.class);
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLeStockEstInsuffisant() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 10, "A1"));

        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.SORTIE, 30, "Vente client"))
                .isInstanceOf(StockInsuffisantException.class);

        Produit inchange = produitRepository.findByReference("REF-001").orElseThrow();
        assertThat(inchange.getQuantite()).isEqualTo(10);
    }

    @Test
    void enregistrerMouvementsEnLot_devraitEnregistrerTousLesMouvements_quandLeLotEstValide() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));
        produitRepository.save(new Produit("REF-002", "Ecrou", "Ecrou 6mm", 30, "A2"));

        List<MouvementLotItem> lot = List.of(
                new MouvementLotItem("REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur"),
                new MouvementLotItem("REF-002", TypeMouvement.SORTIE, 30, "Vente client")
        );

        List<MouvementStock> resultats = mouvementStockService.enregistrerMouvementsEnLot(lot);

        assertThat(resultats).hasSize(2);
        assertThat(produitRepository.findByReference("REF-001").orElseThrow().getQuantite()).isEqualTo(70);
        assertThat(produitRepository.findByReference("REF-002").orElseThrow().getQuantite()).isEqualTo(0);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void enregistrerMouvementsEnLot_devraitAnnulerToutLeLot_quandUnMouvementDuLotEchoue() {
        // NOT_SUPPORTED : desactive la transaction de test (rollback automatique en fin de
        // test) pour que le @Transactional du service soit la seule frontiere reelle, et que
        // le rollback declenche par l'exception soit observable ici, dans le meme test.
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));
        produitRepository.save(new Produit("REF-002", "Ecrou", "Ecrou 6mm", 10, "A2"));

        try {
            List<MouvementLotItem> lot = List.of(
                    new MouvementLotItem("REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur"),
                    new MouvementLotItem("REF-002", TypeMouvement.SORTIE, 30, "Vente client")
            );

            assertThatThrownBy(() -> mouvementStockService.enregistrerMouvementsEnLot(lot))
                    .isInstanceOf(StockInsuffisantException.class);

            Produit produit1 = produitRepository.findByReference("REF-001").orElseThrow();
            assertThat(produit1.getQuantite()).isEqualTo(50);
        } finally {
            produitRepository.deleteAll();
        }
    }
}
