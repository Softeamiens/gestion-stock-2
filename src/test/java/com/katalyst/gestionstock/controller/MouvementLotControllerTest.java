package com.katalyst.gestionstock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katalyst.gestionstock.dto.MouvementLotItem;
import com.katalyst.gestionstock.dto.MouvementLotRequest;
import com.katalyst.gestionstock.entity.MouvementStock;
import com.katalyst.gestionstock.entity.Produit;
import com.katalyst.gestionstock.entity.TypeMouvement;
import com.katalyst.gestionstock.service.MouvementStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MouvementLotControllerTest {

    @Mock
    private MouvementStockService mouvementStockService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MouvementLotController controller = new MouvementLotController(mouvementStockService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void enregistrerMouvementsEnLot_devraitRetourner201_quandLeLotEstValide() throws Exception {
        MouvementLotRequest request = new MouvementLotRequest(List.of(
                new MouvementLotItem("REF-001", TypeMouvement.ENTREE, 20, "Reception fournisseur")
        ));

        Produit produit = new Produit("REF-001", "Vis", "Vis 6mm", 70, "A1");
        MouvementStock mouvement = new MouvementStock(produit, TypeMouvement.ENTREE, 20, "Reception fournisseur");
        when(mouvementStockService.enregistrerMouvementsEnLot(any())).thenReturn(List.of(mouvement));

        mockMvc.perform(post("/api/mouvements/lot")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type").value("ENTREE"))
                .andExpect(jsonPath("$[0].quantiteApresMouvement").value(70));
    }
}
