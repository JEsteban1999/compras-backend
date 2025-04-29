package com.gestor.compras.service;

import java.util.List;

import com.gestor.compras.dto.OrdenCompraDTO;

public interface OrdenCompraService {
    OrdenCompraDTO crearOrden(OrdenCompraDTO ordenCompraDTO);
    List<OrdenCompraDTO> listarOrdenes();
    OrdenCompraDTO obtenerOrden(Long id);
    void eliminarOrden(Long id);
    OrdenCompraDTO actualizarOrden(Long id, OrdenCompraDTO ordenCompraDTO);
    List<Object[]> obtenerTop3Productos();
}
