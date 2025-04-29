package com.gestor.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestor.compras.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Métodos básicos ya incluidos en JpaRepository:
    // save(), findById(), findAll(), deleteById(), etc.
}