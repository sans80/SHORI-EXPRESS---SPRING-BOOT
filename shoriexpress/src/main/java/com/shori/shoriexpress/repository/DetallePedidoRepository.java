package com.shori.shoriexpress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    List<DetallePedido> findByPedido_IdPedido(Long idPedido);
    
    List<DetallePedido> findByProductoTerminado_IdProductoTerminado(Long idProductoTerminado);
}