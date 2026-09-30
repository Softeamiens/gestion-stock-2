package com.katalyst.gestionstock.service;

import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.ReferenceDejaExistanteException;
import com.katalyst.gestionstock.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    private ProduitService produitService;

    @BeforeEach
    void setUp() {
        produitService = new ProduitService(produitRepository);
    }

    @Test
    void creerProduit_devraitCreerLeProduit_quandLaReferenceEstUnique() {
        when(produitRepository.existsByReference("REF-001")).thenReturn(false);
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produit produit = produitService.creerProduit("REF-001", "Vis", "Vis 6mm", 100, "A1");

        assertThat(produit.getReference()).isEqualTo("REF-001");
        assertThat(produit.getQuantite()).isEqualTo(100);
        verify(produitRepository).save(any(Produit.class));
    }

    @Test
    void creerProduit_devraitLeverUneException_quandLaReferenceExisteDeja() {
        when(produitRepository.existsByReference("REF-001")).thenReturn(true);

        assertThatThrownBy(() -> produitService.creerProduit("REF-001", "Vis", "Vis 6mm", 100, "A1"))
                .isInstanceOf(ReferenceDejaExistanteException.class);

        verify(produitRepository, never()).save(any());
    }

    @Test
    void obtenirParReference_devraitRetournerLeProduit_quandIlExiste() {
        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 100, "A1");
        when(produitRepository.findByReference("REF-001")).thenReturn(Optional.of(produit));

        Produit resultat = produitService.obtenirParReference("REF-001");

        assertThat(resultat).isEqualTo(produit);
    }

    @Test
    void obtenirParReference_devraitLeverUneException_quandLeProduitEstIntrouvable() {
        when(produitRepository.findByReference("INCONNU")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.obtenirParReference("INCONNU"))
                .isInstanceOf(ProduitNotFoundException.class);
    }

    @Test
    void listerProduits_devraitFiltrerParEmplacement_quandUnEmplacementEstFourni() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Produit> page = new PageImpl<>(List.of());
        when(produitRepository.findByEmplacement("A1", pageable)).thenReturn(page);

        Page<Produit> resultat = produitService.listerProduits("A1", pageable);

        assertThat(resultat).isSameAs(page);
        verify(produitRepository).findByEmplacement("A1", pageable);
        verify(produitRepository, never()).findAll(pageable);
    }

    @Test
    void listerProduits_devraitRetournerTousLesProduits_quandAucunEmplacementNestFourni() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Produit> page = new PageImpl<>(List.of());
        when(produitRepository.findAll(pageable)).thenReturn(page);

        Page<Produit> resultat = produitService.listerProduits(null, pageable);

        assertThat(resultat).isSameAs(page);
        verify(produitRepository).findAll(pageable);
    }

    @Test
    void compterQuantiteTotale_devraitRetournerLaSommeDesQuantites() {
        when(produitRepository.sommeQuantites()).thenReturn(150L);

        long total = produitService.compterQuantiteTotale();

        assertThat(total).isEqualTo(150L);
    }
}
