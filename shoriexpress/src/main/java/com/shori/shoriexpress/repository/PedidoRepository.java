package com.shori.shoriexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.Pedido;
import com.shori.shoriexpress.model.Pedido.EstadoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuario_IdUsuario(Long idUsuario);
    
    List<Pedido> findByEstadoPedido(EstadoPedido estado);
    
    Optional<Pedido> findByUsuario_IdUsuarioAndEstadoPedido(Long idUsuario, EstadoPedido estado);
    
    List<Pedido> findByUsuario_IdUsuarioOrderByFechaPedidoDesc(Long idUsuario);
    
    List<Pedido> findAllByOrderByFechaPedidoDesc();
}