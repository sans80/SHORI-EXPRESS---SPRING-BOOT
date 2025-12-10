package com.shori.shoriexpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.DetalleProductoFinal;
import com.shori.shoriexpress.repository.DetalleProductoFinalRepository;

@Service
@Transactional
public class DetalleProductoFinalService {

    @Autowired
    private DetalleProductoFinalRepository detalleProductoFinalRepository;

    public List<DetalleProductoFinal> listarTodos() {
        return detalleProductoFinalRepository.findAll();
    }

    public Optional<DetalleProductoFinal> buscarPorId(Long id) {
        return detalleProductoFinalRepository.findById(id);
    }

    public DetalleProductoFinal guardar(DetalleProductoFinal detalleProductoFinal) {
        return detalleProductoFinalRepository.save(detalleProductoFinal);
    }

    public DetalleProductoFinal actualizar(Long id, DetalleProductoFinal detalleProductoFinal) {
        Optional<DetalleProductoFinal> existente = detalleProductoFinalRepository.findById(id);
        if (existente.isPresent()) {
            DetalleProductoFinal detalleActualizado = existente.get();
            detalleActualizado.setMateriaPrima(detalleProductoFinal.getMateriaPrima());
            detalleActualizado.setProductoTerminado(detalleProductoFinal.getProductoTerminado());
            detalleActualizado.setCantidadUtilizada(detalleProductoFinal.getCantidadUtilizada());
            detalleActualizado.setUnidadMedidaDeta(detalleProductoFinal.getUnidadMedidaDeta());
            detalleActualizado.setObservacionesDetalle(detalleProductoFinal.getObservacionesDetalle());
            return detalleProductoFinalRepository.save(detalleActualizado);
        }
        return null;
    }

    public void eliminar(Long id) {
        detalleProductoFinalRepository.deleteById(id);
    }

    public List<DetalleProductoFinal> buscarPorProductoTerminado(Long idProductoTerminado) {
        return detalleProductoFinalRepository.findByProductoTerminado_IdProductoTerminado(idProductoTerminado);
    }

    public List<DetalleProductoFinal> buscarPorMateriaPrima(Long idMateriaPrima) {
        return detalleProductoFinalRepository.findByMateriaPrima_IdMateriaPrima(idMateriaPrima);
    }
}