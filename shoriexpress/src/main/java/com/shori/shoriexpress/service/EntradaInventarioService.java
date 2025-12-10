package com.shori.shoriexpress.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.EntradaInventario;
import com.shori.shoriexpress.model.EntradaInventario.TipoEntrada;
import com.shori.shoriexpress.repository.EntradaInventarioRepository;

@Service
@Transactional
public class EntradaInventarioService {

    @Autowired
    private EntradaInventarioRepository entradaInventarioRepository;

    @Autowired
    private MateriaPrimaService materiaPrimaService;

    public List<EntradaInventario> listarTodas() {
        return entradaInventarioRepository.findAllByOrderByFechaEntradaDesc();
    }

    public Optional<EntradaInventario> buscarPorId(Long id) {
        return entradaInventarioRepository.findById(id);
    }

    public EntradaInventario guardar(EntradaInventario entradaInventario) {
        EntradaInventario entrada = entradaInventarioRepository.save(entradaInventario);
        
        // Actualizar stock de materia prima
        if (entrada.getTipoEntrada() == TipoEntrada.Entrada || entrada.getTipoEntrada() == TipoEntrada.Devolucion) {
            materiaPrimaService.actualizarStock(entrada.getMateriaPrima().getIdMateriaPrima(), 
                                               entrada.getCantidadEntrada());
        }
        
        return entrada;
    }

    public EntradaInventario actualizar(Long id, EntradaInventario entradaInventario) {
        Optional<EntradaInventario> existente = entradaInventarioRepository.findById(id);
        if (existente.isPresent()) {
            EntradaInventario entradaActualizada = existente.get();
            
            // Revertir stock anterior
            int cantidadAnterior = entradaActualizada.getCantidadEntrada();
            materiaPrimaService.actualizarStock(entradaActualizada.getMateriaPrima().getIdMateriaPrima(), 
                                               -cantidadAnterior);
            
            // Actualizar datos
            entradaActualizada.setCantidadEntrada(entradaInventario.getCantidadEntrada());
            entradaActualizada.setPrecioUnitario(entradaInventario.getPrecioUnitario());
            entradaActualizada.setTipoEntrada(entradaInventario.getTipoEntrada());
            entradaActualizada.setDescripcionEntrada(entradaInventario.getDescripcionEntrada());
            entradaActualizada.setMateriaPrima(entradaInventario.getMateriaPrima());
            
            EntradaInventario entrada = entradaInventarioRepository.save(entradaActualizada);
            
            // Aplicar nuevo stock
            materiaPrimaService.actualizarStock(entrada.getMateriaPrima().getIdMateriaPrima(), 
                                               entrada.getCantidadEntrada());
            
            return entrada;
        }
        return null;
    }

    public void eliminar(Long id) {
        Optional<EntradaInventario> entrada = entradaInventarioRepository.findById(id);
        if (entrada.isPresent()) {
            // Revertir stock
            materiaPrimaService.actualizarStock(entrada.get().getMateriaPrima().getIdMateriaPrima(), 
                                               -entrada.get().getCantidadEntrada());
            entradaInventarioRepository.deleteById(id);
        }
    }

    public List<EntradaInventario> buscarPorMateriaPrima(Long idMateriaPrima) {
        return entradaInventarioRepository.findByMateriaPrima_IdMateriaPrima(idMateriaPrima);
    }

    public List<EntradaInventario> buscarPorUsuario(Long idUsuario) {
        return entradaInventarioRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public List<EntradaInventario> buscarPorTipo(TipoEntrada tipoEntrada) {
        return entradaInventarioRepository.findByTipoEntrada(tipoEntrada);
    }

    public List<EntradaInventario> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return entradaInventarioRepository.findByFechaEntradaBetween(fechaInicio, fechaFin);
    }
}