package com.shori.shoriexpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.MetodoPago;
import com.shori.shoriexpress.repository.MetodoPagoRepository;

@Service
@Transactional
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> listarTodos() {
        return metodoPagoRepository.findAll();
    }

    public Optional<MetodoPago> buscarPorId(Long id) {
        return metodoPagoRepository.findById(id);
    }

    public MetodoPago guardar(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    public MetodoPago actualizar(Long id, MetodoPago metodoPago) {
        Optional<MetodoPago> existente = metodoPagoRepository.findById(id);
        if (existente.isPresent()) {
            MetodoPago metodoActualizado = existente.get();
            metodoActualizado.setNombreMetodoPago(metodoPago.getNombreMetodoPago());
            return metodoPagoRepository.save(metodoActualizado);
        }
        return null;
    }

    public void eliminar(Long id) {
        metodoPagoRepository.deleteById(id);
    }

    public Optional<MetodoPago> buscarPorNombre(String nombre) {
        return metodoPagoRepository.findByNombreMetodoPago(nombre);
    }

    public boolean existePorNombre(String nombre) {
        return metodoPagoRepository.existsByNombreMetodoPago(nombre);
    }
}