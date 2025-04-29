package com.gestor.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestor.compras.model.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    // Métodos básicos automáticos
}
