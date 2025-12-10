package com.shori.shoriexpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.MateriaPrima;
import com.shori.shoriexpress.model.MateriaPrima.EstadoMateriaPrima;
import com.shori.shoriexpress.repository.MateriaPrimaRepository;

@Service
@Transactional
public class MateriaPrimaService {

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    public List<MateriaPrima> listarTodas() {
        return materiaPrimaRepository.findAll();
    }

    public Optional<MateriaPrima> buscarPorId(Long id) {
        return materiaPrimaRepository.findById(id);
    }

    public MateriaPrima guardar(MateriaPrima materiaPrima) {
        return materiaPrimaRepository.save(materiaPrima);
    }

    public MateriaPrima actualizar(Long id, MateriaPrima materiaPrima) {
        Optional<MateriaPrima> existente = materiaPrimaRepository.findById(id);
        if (existente.isPresent()) {
            MateriaPrima materiaPrimaActualizada = existente.get();
            materiaPrimaActualizada.setNombreMateriaPrima(materiaPrima.getNombreMateriaPrima());
            materiaPrimaActualizada.setCategoriaMateriaPrima(materiaPrima.getCategoriaMateriaPrima());
            materiaPrimaActualizada.setUnidadMedidaMateriaPrima(materiaPrima.getUnidadMedidaMateriaPrima());
            materiaPrimaActualizada.setDescripcionMateriaPrima(materiaPrima.getDescripcionMateriaPrima());
            materiaPrimaActualizada.setPrecioMateriaPrima(materiaPrima.getPrecioMateriaPrima());
            materiaPrimaActualizada.setStockMateriaPrima(materiaPrima.getStockMateriaPrima());
            materiaPrimaActualizada.setEstadoMateriaPrima(materiaPrima.getEstadoMateriaPrima());
            materiaPrimaActualizada.setFechaVencimientoMateriaPrima(materiaPrima.getFechaVencimientoMateriaPrima());
            return materiaPrimaRepository.save(materiaPrimaActualizada);
        }
        return null;
    }

    public void eliminar(Long id) {
        materiaPrimaRepository.deleteById(id);
    }

    public List<MateriaPrima> buscarPorNombre(String nombre) {
        return materiaPrimaRepository.findByNombreMateriaPrimaContainingIgnoreCase(nombre);
    }

    public List<MateriaPrima> buscarPorCategoria(String categoria) {
        return materiaPrimaRepository.findByCategoriaMateriaPrima(categoria);
    }

    public List<MateriaPrima> buscarPorEstado(EstadoMateriaPrima estado) {
        return materiaPrimaRepository.findByEstadoMateriaPrima(estado);
    }

    public List<MateriaPrima> buscarStockBajo(Integer minStock) {
        return materiaPrimaRepository.findByStockBajo(minStock);
    }

    public List<String> listarCategorias() {
        return materiaPrimaRepository.findAllCategorias();
    }

    public void actualizarStock(Long id, Integer cantidad) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(id);
        if (materiaPrima.isPresent()) {
            MateriaPrima mp = materiaPrima.get();
            mp.setStockMateriaPrima(mp.getStockMateriaPrima() + cantidad);
            if (mp.getStockMateriaPrima() <= 0) {
                mp.setEstadoMateriaPrima(EstadoMateriaPrima.agotada);
            } else {
                mp.setEstadoMateriaPrima(EstadoMateriaPrima.disponible);
            }
            materiaPrimaRepository.save(mp);
        }
    }

    public MateriaPrima obtenerPorId(Long id) {
    // Asume que tienes MateriaPrimaRepository inyectado
    return materiaPrimaRepository.findById(id).orElse(null);
}
}