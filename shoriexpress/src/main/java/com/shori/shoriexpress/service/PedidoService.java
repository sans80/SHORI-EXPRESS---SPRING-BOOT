package com.shori.shoriexpress.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.Pedido;
import com.shori.shoriexpress.model.Pedido.EstadoPedido;
import com.shori.shoriexpress.repository.PedidoRepository;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido pedido) {
        Optional<Pedido> existente = pedidoRepository.findById(id);
        if (existente.isPresent()) {
            Pedido pedidoActualizado = existente.get();
            pedidoActualizado.setDescripcionPedido(pedido.getDescripcionPedido());
            pedidoActualizado.setEstadoPedido(pedido.getEstadoPedido());
            pedidoActualizado.setTotalPedido(pedido.getTotalPedido());
            return pedidoRepository.save(pedidoActualizado);
        }
        return null;
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> buscarPorUsuario(Long idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuarioOrderByFechaPedidoDesc(idUsuario);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoPedido(estado);
    }

    public Optional<Pedido> obtenerCarritoActivo(Long idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuarioAndEstadoPedido(idUsuario, EstadoPedido.carrito);
    }

    public Pedido actualizarTotal(Long id, BigDecimal total) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            p.setTotalPedido(total);
            return pedidoRepository.save(p);
        }
        return null;
    }

    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            p.setEstadoPedido(nuevoEstado);
            return pedidoRepository.save(p);
        }
        return null;
    }
}