package com.katalyst.gestionstock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katalyst.gestionstock.dto.ProduitCreateRequest;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.exception.GlobalExceptionHandler;
import com.katalyst.gestionstock.exception.ProduitNotFoundException;
import com.katalyst.gestionstock.exception.ReferenceDejaExistanteException;
import com.katalyst.gestionstock.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProduitControllerTest {

    @Mock
    private ProduitService produitService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ProduitController controller = new ProduitController(produitService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void creerProduit_devraitRetourner201_quandLaCreationReussit() throws Exception {
        ProduitCreateRequest request = new ProduitCreateRequest("REF-001", "Vis", "Vis 6mm", 100, "A1");

        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 100, "A1");
        when(produitService.creerProduit("REF-001", "Vis", "Vis 6mm", 100, "A1")).thenReturn(produit);

        mockMvc.perform(post("/api/produits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("REF-001"))
                .andExpect(jsonPath("$.quantite").value(100));
    }

    @Test
    void creerProduit_devraitRetourner400_quandLaRequeteEstInvalide() throws Exception {
        ProduitCreateRequest request = new ProduitCreateRequest(null, "Vis", null, 100, "A1");

        mockMvc.perform(post("/api/produits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.reference").value("La reference est obligatoire"));
    }

    @Test
    void creerProduit_devraitRetourner409_quandLaReferenceExisteDeja() throws Exception {
        ProduitCreateRequest request = new ProduitCreateRequest("REF-001", "Vis", null, 100, "A1");

        when(produitService.creerProduit(anyString(), anyString(), any(), any(), anyString()))
                .thenThrow(new ReferenceDejaExistanteException("REF-001"));

        mockMvc.perform(post("/api/produits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Un produit existe deja avec la reference : REF-001"));
    }

    @Test
    void obtenirProduit_devraitRetourner200_quandLeProduitExiste() throws Exception {
        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 100, "A1");
        when(produitService.obtenirParReference("REF-001")).thenReturn(produit);

        mockMvc.perform(get("/api/produits/REF-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("REF-001"));
    }

    @Test
    void obtenirProduit_devraitRetourner404_quandLeProduitEstIntrouvable() throws Exception {
        when(produitService.obtenirParReference("INCONNU")).thenThrow(new ProduitNotFoundException("INCONNU"));

        mockMvc.perform(get("/api/produits/INCONNU"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Aucun produit trouve pour la reference : INCONNU"));
    }

    @Test
    void listerProduits_devraitRetourner200_avecUnePageDeProduits() throws Exception {
        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 100, "A1");
        Page<Produit> page = new PageImpl<>(List.of(produit), PageRequest.of(0, 20), 1);
        when(produitService.listerProduits(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("REF-001"));
    }
}
