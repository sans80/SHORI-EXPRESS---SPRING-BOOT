package com.shori.shoriexpress.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.RedencionBono;
import com.shori.shoriexpress.model.Usuario;

@Repository
public interface RedencionBonoRepository extends JpaRepository<RedencionBono, Long> {
    
    List<RedencionBono> findByUsuario(Usuario usuario);
    
    List<RedencionBono> findByFechaRedencionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT r FROM RedencionBono r WHERE " +
           "(:usuario IS NULL OR r.usuario = :usuario) AND " +
           "(:fechaInicio IS NULL OR r.fechaRedencion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR r.fechaRedencion <= :fechaFin)")
    List<RedencionBono> buscarRedenciones(@Param("usuario") Usuario usuario,
                                        @Param("fechaInicio") LocalDateTime fechaInicio,
                                        @Param("fechaFin") LocalDateTime fechaFin);
    
    Long countByUsuario(Usuario usuario);
}