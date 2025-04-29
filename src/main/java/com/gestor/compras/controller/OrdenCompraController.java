package com.gestor.compras.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestor.compras.dto.OrdenCompraDTO;
import com.gestor.compras.service.OrdenCompraService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @PostMapping
    public ResponseEntity<OrdenCompraDTO> crearOrden(@Valid @RequestBody OrdenCompraDTO ordenCompraDTO) {
        OrdenCompraDTO nuevaOrden = ordenCompraService.crearOrden(ordenCompraDTO);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrdenCompraDTO>> listarOrdenes() {
        List<OrdenCompraDTO> ordenes = ordenCompraService.listarOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> obtenerOrden(@PathVariable Long id) {
        OrdenCompraDTO orden = ordenCompraService.obtenerOrden(id);
        return ResponseEntity.ok(orden);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenCompraService.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos/top3")
    public ResponseEntity<List<Object[]>> obtenerTop3Productos() {
        List<Object[]> topProductos = ordenCompraService.obtenerTop3Productos();
        return ResponseEntity.ok(topProductos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> actualizarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrdenCompraDTO ordenCompraDTO) {
        OrdenCompraDTO ordenActualizada = ordenCompraService.actualizarOrden(id, ordenCompraDTO);
        return ResponseEntity.ok(ordenActualizada);
    }
}
