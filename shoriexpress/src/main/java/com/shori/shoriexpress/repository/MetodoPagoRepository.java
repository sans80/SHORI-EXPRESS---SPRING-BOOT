package com.shori.shoriexpress.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.MetodoPago;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    
    Optional<MetodoPago> findByNombreMetodoPago(String nombreMetodoPago);
    
    boolean existsByNombreMetodoPago(String nombreMetodoPago);
}