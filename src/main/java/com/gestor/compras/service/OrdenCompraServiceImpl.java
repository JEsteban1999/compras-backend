package com.gestor.compras.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestor.compras.dto.DetalleOrdenDTO;
import com.gestor.compras.dto.OrdenCompraDTO;
import com.gestor.compras.exception.BusinessException;
import com.gestor.compras.exception.ResourceNotFoundException;
import com.gestor.compras.model.DetalleOrden;
import com.gestor.compras.model.OrdenCompra;
import com.gestor.compras.model.Producto;
import com.gestor.compras.repository.DetalleOrdenRepository;
import com.gestor.compras.repository.OrdenCompraRepository;
import com.gestor.compras.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoRepository productoRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrdenCompraDTO crearOrden(OrdenCompraDTO ordenCompraDTO) {
        // Validar stock antes de procesar la orden
        validarStockDisponible(ordenCompraDTO.getDetalles());

        BigDecimal totalCalculado = calcularTotal(ordenCompraDTO.getDetalles());
        OrdenCompra orden = new OrdenCompra();
        orden.setNumeroOrden(generarNumeroOrden());
        orden.setTotal(totalCalculado);

        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);

        ordenCompraDTO.getDetalles().forEach(detalleDTO -> {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            // Actualizar stock del producto

            int nuevoStock = producto.getStock() - detalleDTO.getCantidad();
            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(ordenGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setSubtotal(detalleDTO.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));

            detalleOrdenRepository.save(detalle);
        });

        return modelMapper.map(ordenGuardada, OrdenCompraDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> listarOrdenes() {
        return ordenCompraRepository.findAll().stream()
                .map(orden -> modelMapper.map(orden, OrdenCompraDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraDTO obtenerOrden(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
        return modelMapper.map(orden, OrdenCompraDTO.class);
    }

    @Override
    @Transactional
    public void eliminarOrden(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // Restaurar el stock de los productos
        orden.getDetalles().forEach(detalle -> {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        });
        ordenCompraRepository.delete(orden);
    }

    @Override
    @Transactional
    public OrdenCompraDTO actualizarOrden(Long id, OrdenCompraDTO ordenCompraDTO) {
        OrdenCompra ordenExistente = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // Primero restaurar el stock de los detalles antiguos
        ordenExistente.getDetalles().forEach(detalle -> {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        });

        // Validar nuevo stock disponible
        validarStockDisponible(ordenCompraDTO.getDetalles());

        // Actualizar el total
        ordenExistente.setTotal(calcularTotal(ordenCompraDTO.getDetalles()));

        // Eliminar los detalles existentes
        detalleOrdenRepository.deleteByOrdenId(id);

        // Crear nuevos detalles y actualizar stock
        ordenCompraDTO.getDetalles().forEach(detalleDTO -> {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            // Actualizar stock
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto);

            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(ordenExistente);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setSubtotal(detalleDTO.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));

            detalleOrdenRepository.save(detalle);
        });

        OrdenCompra ordenGuardada = ordenCompraRepository.save(ordenExistente);
        return modelMapper.map(ordenGuardada, OrdenCompraDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> obtenerTop3Productos() {
        return detalleOrdenRepository.findTop3ProductosMasVendidos();
    }

    private BigDecimal calcularTotal(List<DetalleOrdenDTO> detalles) {
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método para validar el stock disponible
    private void validarStockDisponible(List<DetalleOrdenDTO> detalles) {
        detalles.forEach(detalleDTO -> {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Stock disponible: " + producto.getStock() +
                        ", cantidad solicitada: " + detalleDTO.getCantidad());
            }
        });
    }

    private String generarNumeroOrden() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
