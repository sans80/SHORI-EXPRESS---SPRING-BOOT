package com.shori.shoriexpress.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.ProductoTerminado;
import com.shori.shoriexpress.repository.ProductoTerminadoRepository;

@Service
@Transactional
public class ProductoTerminadoService {

    @Autowired
    private ProductoTerminadoRepository productoTerminadoRepository;

    public List<ProductoTerminado> listarTodos() {
        return productoTerminadoRepository.findAll();
    }

    public Optional<ProductoTerminado> buscarPorId(Long id) {
        return productoTerminadoRepository.findById(id);
    }

    public ProductoTerminado guardar(ProductoTerminado producto) {
        return productoTerminadoRepository.save(producto);
    }

    public ProductoTerminado actualizar(Long id, ProductoTerminado datosActualizados) {
        Optional<ProductoTerminado> productoOpt = productoTerminadoRepository.findById(id);
        if (productoOpt.isPresent()) {
            ProductoTerminado producto = productoOpt.get();
            
            producto.setNombreProductoTerminado(datosActualizados.getNombreProductoTerminado());
            producto.setDescripcionProductoTerminado(datosActualizados.getDescripcionProductoTerminado());
            producto.setCantidadProductoTerminado(datosActualizados.getCantidadProductoTerminado());
            producto.setStockProductoTerminado(datosActualizados.getStockProductoTerminado());
            producto.setPrecioVentaProductoTerminado(datosActualizados.getPrecioVentaProductoTerminado());
            
            return productoTerminadoRepository.save(producto);
        }
        return null;
    }

    public void eliminar(Long id) {
        productoTerminadoRepository.deleteById(id);
    }

    public List<ProductoTerminado> buscarPorNombre(String nombre) {
        return productoTerminadoRepository.findByNombreProductoTerminadoContainingIgnoreCase(nombre);
    }

    public List<ProductoTerminado> buscarConStock() {
        return productoTerminadoRepository.findByStockProductoTerminadoGreaterThan(0);
    }

    public List<ProductoTerminado> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return productoTerminadoRepository.findByPrecioVentaProductoTerminadoBetween(precioMin, precioMax);
    }

    public List<ProductoTerminado> buscarProductos(String nombre, BigDecimal precioMin, BigDecimal precioMax) {
        return productoTerminadoRepository.buscarProductos(nombre, precioMin, precioMax);
    }

    public boolean actualizarStock(Long id, Integer nuevoStock) {
        Optional<ProductoTerminado> productoOpt = productoTerminadoRepository.findById(id);
        if (productoOpt.isPresent()) {
            ProductoTerminado producto = productoOpt.get();
            producto.setStockProductoTerminado(nuevoStock);
            productoTerminadoRepository.save(producto);
            return true;
        }
        return false;
    }
}