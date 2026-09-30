package com.katalyst.gestionstock.repository;

import com.katalyst.gestionstock.entity.Produit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProduitRepositoryTest {

    @Autowired
    private ProduitRepository produitRepository;

    @Test
    void incrementerQuantite_devraitAugmenterLaQuantiteEnBase() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));

        int lignesMisesAJour = produitRepository.incrementerQuantite("REF-001", 20);

        assertThat(lignesMisesAJour).isEqualTo(1);
        Produit rechargee = produitRepository.findByReference("REF-001").orElseThrow();
        assertThat(rechargee.getQuantite()).isEqualTo(70);
    }

    @Test
    void incrementerQuantite_neDevraitRienModifier_quandLaReferenceEstInconnue() {
        int lignesMisesAJour = produitRepository.incrementerQuantite("INCONNU", 20);

        assertThat(lignesMisesAJour).isEqualTo(0);
    }

    @Test
    void decrementerQuantiteSiSuffisant_devraitDiminuerLaQuantite_quandLeStockEstSuffisant() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));

        int lignesMisesAJour = produitRepository.decrementerQuantiteSiSuffisant("REF-001", 30);

        assertThat(lignesMisesAJour).isEqualTo(1);
        Produit rechargee = produitRepository.findByReference("REF-001").orElseThrow();
        assertThat(rechargee.getQuantite()).isEqualTo(20);
    }

    @Test
    void decrementerQuantiteSiSuffisant_neDevraitRienModifier_quandLeStockEstInsuffisant() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 10, "A1"));

        int lignesMisesAJour = produitRepository.decrementerQuantiteSiSuffisant("REF-001", 30);

        assertThat(lignesMisesAJour).isEqualTo(0);
        Produit rechargee = produitRepository.findByReference("REF-001").orElseThrow();
        assertThat(rechargee.getQuantite()).isEqualTo(10);
    }

    @Test
    void sommeQuantites_devraitAdditionnerLesQuantitesDeTousLesProduits() {
        produitRepository.save(new Produit("REF-001", "Vis", "Vis 6mm", 50, "A1"));
        produitRepository.save(new Produit("REF-002", "Ecrou", "Ecrou 6mm", 30, "A2"));

        long total = produitRepository.sommeQuantites();

        assertThat(total).isEqualTo(80L);
    }

    @Test
    void sommeQuantites_devraitRetournerZero_quandAucunProduitNexiste() {
        long total = produitRepository.sommeQuantites();

        assertThat(total).isZero();
    }
}
