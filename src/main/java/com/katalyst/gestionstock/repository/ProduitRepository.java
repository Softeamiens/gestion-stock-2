package com.katalyst.gestionstock.repository;

import com.katalyst.gestionstock.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByReference(String reference);

    boolean existsByReference(String reference);

    Page<Produit> findByEmplacement(String emplacement, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Produit p set p.quantite = p.quantite + :quantite where p.reference = :reference")
    int incrementerQuantite(@Param("reference") String reference, @Param("quantite") Integer quantite);

    @Modifying(clearAutomatically = true)
    @Query("update Produit p set p.quantite = p.quantite - :quantite "
            + "where p.reference = :reference and p.quantite >= :quantite")
    int decrementerQuantiteSiSuffisant(@Param("reference") String reference, @Param("quantite") Integer quantite);

    @Query("select coalesce(sum(p.quantite), 0) from Produit p")
    long sommeQuantites();
}
