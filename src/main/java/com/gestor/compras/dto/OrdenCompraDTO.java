package com.gestor.compras.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdenCompraDTO {
    private Long id; // Solo para respuestas
    
    // Generado automáticamente (no se incluye en requests)
    private String numeroOrden;
    
    //@NotNull(message = "El total es requerido")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private BigDecimal total;
    
    @NotEmpty(message = "Debe haber al menos un producto")
    @Valid // Para validar los objetos dentro de la lista
    private List<DetalleOrdenDTO> detalles;
}
