package com.shori.shoriexpress.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.EntradaInventario;
import com.shori.shoriexpress.model.EntradaInventario.TipoEntrada;

@Repository
public interface EntradaInventarioRepository extends JpaRepository<EntradaInventario, Long> {
    
    List<EntradaInventario> findByMateriaPrima_IdMateriaPrima(Long idMateriaPrima);
    
    List<EntradaInventario> findByUsuario_IdUsuario(Long idUsuario);
    
    List<EntradaInventario> findByTipoEntrada(TipoEntrada tipoEntrada);
    
    @Query("SELECT e FROM EntradaInventario e WHERE e.fechaEntrada BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaEntrada DESC")
    List<EntradaInventario> findByFechaEntradaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    List<EntradaInventario> findAllByOrderByFechaEntradaDesc();
}