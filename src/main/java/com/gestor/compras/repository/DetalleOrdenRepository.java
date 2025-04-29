package com.gestor.compras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.gestor.compras.model.DetalleOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM DetalleOrden d WHERE d.orden.id = :ordenId")
    void deleteByOrdenId(Long ordenId);

    @Query("SELECT p.id, p.nombre, SUM(d.cantidad) as total " +
            "FROM DetalleOrden d JOIN d.producto p " +
            "GROUP BY p.id, p.nombre " +
            "ORDER BY total DESC LIMIT 3")
    List<Object[]> findTop3ProductosMasVendidos();
}
