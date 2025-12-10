package com.shori.shoriexpress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.Bono;
import com.shori.shoriexpress.model.Usuario;

@Repository
public interface BonoRepository extends JpaRepository<Bono, Long> {
    
    List<Bono> findByUsuario(Usuario usuario);
    
    List<Bono> findByEstadoBono(Bono.EstadoBono estado);
    
    List<Bono> findByUsuarioAndEstadoBono(Usuario usuario, Bono.EstadoBono estado);
    
    @Query("SELECT b FROM Bono b WHERE " +
           "(:usuario IS NULL OR b.usuario = :usuario) AND " +
           "(:estado IS NULL OR b.estadoBono = :estado)")
    List<Bono> buscarBonos(@Param("usuario") Usuario usuario, 
                          @Param("estado") Bono.EstadoBono estado);

    public Object findByUsuario_IdUsuario(Long idUsuario);
}