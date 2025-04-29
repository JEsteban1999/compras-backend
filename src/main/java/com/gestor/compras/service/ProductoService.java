package com.gestor.compras.service;

import java.util.List;

import com.gestor.compras.dto.ProductoDTO;

public interface ProductoService {
    ProductoDTO crearProducto(ProductoDTO productoDTO);
    List<ProductoDTO> listarProductos();
    ProductoDTO obtenerProducto(Long id);
    ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO);
    void eliminarProducto(Long id);
}
