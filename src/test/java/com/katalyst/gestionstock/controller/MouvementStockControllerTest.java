package com.katalyst.gestionstock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katalyst.gestionstock.dto.MouvementCreateRequest;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.entity.TypeMouvement;
import com.katalyst.gestionstock.exception.GlobalExceptionHandler;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.StockInsuffisantException;
import com.katalyst.gestionstock.service.MouvementStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MouvementStockControllerTest {

    @Mock
    private MouvementStockService mouvementStockService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MouvementStockController controller = new MouvementStockController(mouvementStockService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void enregistrerMouvement_devraitRetourner201_quandLeMouvementEstValide() throws Exception {
        MouvementCreateRequest request = new MouvementCreateRequest(TypeMouvement.ENTREE, 10, "Reception fournisseur");

        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 60, "A1");
        MouvementStock mouvement = new MouvementStock(produit, TypeMouvement.ENTREE, 10, "Reception fournisseur");

        when(mouvementStockService.enregistrerMouvement("REF-001", TypeMouvement.ENTREE, 10, "Reception fournisseur"))
                .thenReturn(mouvement);

        mockMvc.perform(post("/api/produits/REF-001/mouvements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ENTREE"))
                .andExpect(jsonPath("$.quantiteApresMouvement").value(60));
    }

    @Test
    void enregistrerMouvement_devraitRetourner404_quandLeProduitEstIntrouvable() throws Exception {
        MouvementCreateRequest request = new MouvementCreateRequest(TypeMouvement.ENTREE, 10, "Reception fournisseur");

        when(mouvementStockService.enregistrerMouvement(anyString(), any(), any(), anyString()))
                .thenThrow(new ProduitNotFoundException("INCONNU"));

        mockMvc.perform(post("/api/produits/INCONNU/mouvements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Aucun produit trouve pour la reference : INCONNU"))
                .andExpect(jsonPath("$.instance").value("/api/produits/INCONNU/mouvements"));
    }

    @Test
    void enregistrerMouvement_devraitRetourner409_quandLeStockEstInsuffisant() throws Exception {
        MouvementCreateRequest request = new MouvementCreateRequest(TypeMouvement.SORTIE, 100, "Vente client");

        when(mouvementStockService.enregistrerMouvement(anyString(), any(), any(), anyString()))
                .thenThrow(new StockInsuffisantException("REF-001", 10, 100));

        mockMvc.perform(post("/api/produits/REF-001/mouvements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Stock insuffisant pour le produit REF-001 : disponible 10, demande 100"));
    }

    @Test
    void enregistrerMouvement_devraitRetourner400_quandLaQuantiteEstNegative() throws Exception {
        MouvementCreateRequest request = new MouvementCreateRequest(TypeMouvement.SORTIE, -5, "Vente client");

        mockMvc.perform(post("/api/produits/REF-001/mouvements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.quantite").value("La quantite doit etre strictement positive"));
    }
}
