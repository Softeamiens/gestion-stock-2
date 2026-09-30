package com.katalyst.gestionstock.repository;

import com.katalyst.gestionstock.entity.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
}
