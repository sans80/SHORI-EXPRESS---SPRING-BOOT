package com.shori.shoriexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    Optional<Factura> findByPedido_IdPedido(Long idPedido);
    
    List<Factura> findByMetodoPago_IdMetodoPago(Long idMetodoPago);
    
    List<Factura> findAllByOrderByFechaEmisionDesc();
    
    boolean existsByNumeroFactura(String numeroFactura);
}