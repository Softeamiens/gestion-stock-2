package com.katalyst.gestionstock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement type;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    protected MouvementStock() {
    }

    public MouvementStock(Produit produit, TypeMouvement type, Integer quantite, String motif) {
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
    }

    @PrePersist
    void avantPersistance() {
        this.dateMouvement = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Produit getProduit() {
        return produit;
    }

    public TypeMouvement getType() {
        return type;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public String getMotif() {
        return motif;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }
}
