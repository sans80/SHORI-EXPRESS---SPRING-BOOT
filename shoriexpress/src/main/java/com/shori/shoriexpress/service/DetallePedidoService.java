package com.shori.shoriexpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.DetallePedido;
import com.shori.shoriexpress.repository.DetallePedidoRepository;

@Service
@Transactional
public class DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    public List<DetallePedido> listarTodos() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public DetallePedido guardar(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    public DetallePedido actualizar(Long id, DetallePedido detallePedido) {
        Optional<DetallePedido> existente = detallePedidoRepository.findById(id);
        if (existente.isPresent()) {
            DetallePedido detalleActualizado = existente.get();
            detalleActualizado.setPedido(detallePedido.getPedido());
            detalleActualizado.setProductoTerminado(detallePedido.getProductoTerminado());
            detalleActualizado.setCantidadDetallePedido(detallePedido.getCantidadDetallePedido());
            return detallePedidoRepository.save(detalleActualizado);
        }
        return null;
    }

    public void eliminar(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    public List<DetallePedido> buscarPorPedido(Long idPedido) {
        return detallePedidoRepository.findByPedido_IdPedido(idPedido);
    }

    public List<DetallePedido> buscarPorProducto(Long idProductoTerminado) {
        return detallePedidoRepository.findByProductoTerminado_IdProductoTerminado(idProductoTerminado);
    }
}