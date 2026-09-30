package com.katalyst.gestionstock.service;

import com.katalyst.gestionstock.dto.MouvementLotItem;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.entity.TypeMouvement;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.StockInsuffisantException;
import com.katalyst.gestionstock.repository.MouvementStockRepository;
import com.katalyst.gestionstock.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MouvementStockServiceTest {

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @Mock
    private ProduitRepository produitRepository;

    private MouvementStockService mouvementStockService;

    @BeforeEach
    void setUp() {
        mouvementStockService = new MouvementStockService(mouvementStockRepository, produitRepository);
    }

    @Test
    void enregistrerMouvement_devraitAugmenterLaQuantite_quandLeTypeEstEntree() {
        Produit produitApresMiseAJour = new Produit("REF-001", "Vis", "Vis 6mm", 70, "A1");
        when(produitRepository.incrementerQuantite("REF-001", 20)).thenReturn(1);
        when(produitRepository.findByReference("REF-001")).thenReturn(Optional.of(produitApresMiseAJour));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MouvementStock mouvement = mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur");

        verify(produitRepository).incrementerQuantite("REF-001", 20);
        assertThat(mouvement.getType()).isEqualTo(TypeMouvement.ENTREE);
        assertThat(mouvement.getProduit().getQuantite()).isEqualTo(70);
    }

    @Test
    void enregistrerMouvement_devraitDiminuerLaQuantite_quandLaSortieEstPossible() {
        Produit produitApresMiseAJour = new Produit("REF-001", "Vis", "Vis 6mm", 20, "A1");
        when(produitRepository.decrementerQuantiteSiSuffisant("REF-001", 30)).thenReturn(1);
        when(produitRepository.findByReference("REF-001")).thenReturn(Optional.of(produitApresMiseAJour));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MouvementStock mouvement = mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.SORTIE, 30, "Vente client");

        verify(produitRepository).decrementerQuantiteSiSuffisant("REF-001", 30);
        assertThat(mouvement.getProduit().getQuantite()).isEqualTo(20);
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLaSortieRendLaQuantiteNegative() {
        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 10, "A1");
        when(produitRepository.decrementerQuantiteSiSuffisant("REF-001", 30)).thenReturn(0);
        when(produitRepository.findByReference("REF-001")).thenReturn(Optional.of(produit));

        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "REF-001", TypeMouvement.SORTIE, 30, "Vente client"))
                .isInstanceOf(StockInsuffisantException.class);

        verify(mouvementStockRepository, never()).save(any());
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLeProduitEstIntrouvablePourUneEntree() {
        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "INCONNU", TypeMouvement.ENTREE, 10, "Reception"))
                .isInstanceOf(ProduitNotFoundException.class);

        verify(mouvementStockRepository, never()).save(any());
    }

    @Test
    void enregistrerMouvement_devraitLeverUneException_quandLeProduitEstIntrouvablePourUneSortie() {
        when(produitRepository.decrementerQuantiteSiSuffisant("INCONNU", 10)).thenReturn(0);
        when(produitRepository.findByReference("INCONNU")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mouvementStockService.enregistrerMouvement(
                "INCONNU", TypeMouvement.SORTIE, 10, "Vente client"))
                .isInstanceOf(ProduitNotFoundException.class);

        verify(mouvementStockRepository, never()).save(any());
    }

    @Test
    void enregistrerMouvementsEnLot_devraitEnregistrerChaqueMouvementDuLot() {
        Produit produitApresEntree = new Produit("REF-001", "Vis", "Vis 6mm", 70, "A1");
        Produit produitApresSortie = new Produit("REF-002", "Ecrou", "Ecrou 6mm", 20, "A2");
        when(produitRepository.incrementerQuantite("REF-001", 20)).thenReturn(1);
        when(produitRepository.findByReference("REF-001")).thenReturn(Optional.of(produitApresEntree));
        when(produitRepository.decrementerQuantiteSiSuffisant("REF-002", 30)).thenReturn(1);
        when(produitRepository.findByReference("REF-002")).thenReturn(Optional.of(produitApresSortie));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<MouvementLotItem> lot = List.of(
                new MouvementLotItem("REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur"),
                new MouvementLotItem("REF-002", TypeMouvement.SORTIE, 30, "Vente client")
        );

        List<MouvementStock> resultats = mouvementStockService.enregistrerMouvementsEnLot(lot);

        assertThat(resultats).hasSize(2);
        assertThat(resultats.get(0).getProduit().getQuantite()).isEqualTo(70);
        assertThat(resultats.get(1).getProduit().getQuantite()).isEqualTo(20);
    }
}
