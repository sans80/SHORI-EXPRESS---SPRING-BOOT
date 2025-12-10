package com.shori.shoriexpress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.DetalleProductoFinal;

@Repository
public interface DetalleProductoFinalRepository extends JpaRepository<DetalleProductoFinal, Long> {
    
    List<DetalleProductoFinal> findByProductoTerminado_IdProductoTerminado(Long idProductoTerminado);
    
    List<DetalleProductoFinal> findByMateriaPrima_IdMateriaPrima(Long idMateriaPrima);
}