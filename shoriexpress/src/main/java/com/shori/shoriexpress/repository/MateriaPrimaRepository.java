package com.shori.shoriexpress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.MateriaPrima;
import com.shori.shoriexpress.model.MateriaPrima.EstadoMateriaPrima;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {
    
    List<MateriaPrima> findByNombreMateriaPrimaContainingIgnoreCase(String nombre);
    
    List<MateriaPrima> findByCategoriaMateriaPrima(String categoria);
    
    List<MateriaPrima> findByEstadoMateriaPrima(EstadoMateriaPrima estado);
    
    @Query("SELECT m FROM MateriaPrima m WHERE m.stockMateriaPrima <= :minStock")
    List<MateriaPrima> findByStockBajo(@Param("minStock") Integer minStock);
    
    @Query("SELECT DISTINCT m.categoriaMateriaPrima FROM MateriaPrima m ORDER BY m.categoriaMateriaPrima")
    List<String> findAllCategorias();
}