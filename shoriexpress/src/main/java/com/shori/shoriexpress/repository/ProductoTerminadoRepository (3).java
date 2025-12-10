package com.shori.shoriexpress.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.ProductoTerminado;

@Repository
public interface ProductoTerminadoRepository extends JpaRepository<ProductoTerminado, Long> {
    
    List<ProductoTerminado> findByNombreProductoTerminadoContainingIgnoreCase(String nombre);
    
    List<ProductoTerminado> findByStockProductoTerminadoGreaterThan(Integer stock);
    
    List<ProductoTerminado> findByPrecioVentaProductoTerminadoBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    @Query("SELECT p FROM ProductoTerminado p WHERE " +
           "(:nombre IS NULL OR p.nombreProductoTerminado LIKE %:nombre%) AND " +
           "(:precioMin IS NULL OR p.precioVentaProductoTerminado >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precioVentaProductoTerminado <= :precioMax)")
    List<ProductoTerminado> buscarProductos(@Param("nombre") String nombre, 
                                          @Param("precioMin") BigDecimal precioMin, 
                                          @Param("precioMax") BigDecimal precioMax);
}