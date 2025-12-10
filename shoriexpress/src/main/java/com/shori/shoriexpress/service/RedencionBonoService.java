package com.shori.shoriexpress.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.Bono;
import com.shori.shoriexpress.model.RedencionBono;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.repository.BonoRepository;
import com.shori.shoriexpress.repository.RedencionBonoRepository;

@Service
@Transactional
public class RedencionBonoService {

    @Autowired
    private RedencionBonoRepository redencionBonoRepository;

    @Autowired
    private BonoRepository bonoRepository;

    public List<RedencionBono> listarTodas() {
        return redencionBonoRepository.findAll();
    }

    public Optional<RedencionBono> buscarPorId(Long id) {
        return redencionBonoRepository.findById(id);
    }

    public RedencionBono guardar(RedencionBono redencion) {
        return redencionBonoRepository.save(redencion);
    }

    public void eliminar(Long id) {
        redencionBonoRepository.deleteById(id);
    }

    public List<RedencionBono> buscarPorUsuario(Usuario usuario) {
        return redencionBonoRepository.findByUsuario(usuario);
    }

    public List<RedencionBono> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return redencionBonoRepository.findByFechaRedencionBetween(fechaInicio, fechaFin);
    }

    public List<RedencionBono> buscarRedenciones(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return redencionBonoRepository.buscarRedenciones(usuario, fechaInicio, fechaFin);
    }

    public Long contarRedencionesUsuario(Usuario usuario) {
        return redencionBonoRepository.countByUsuario(usuario);
    }

    public RedencionBono redimirBono(Long idBono, Usuario usuario) {
        Optional<Bono> bonoOpt = bonoRepository.findById(idBono);
        if (bonoOpt.isPresent()) {
            Bono bono = bonoOpt.get();
            
            // Verificar que el bono esté disponible y pertenezca al usuario
            if (bono.getEstadoBono() == Bono.EstadoBono.disponible && 
                bono.getUsuario().equals(usuario)) {
                
                // Crear la redención
                RedencionBono redencion = new RedencionBono();
                redencion.setBono(bono);
                redencion.setUsuario(usuario);
                
                // Marcar el bono como redimido
                bono.setEstadoBono(Bono.EstadoBono.redimido);
                bonoRepository.save(bono);
                
                return redencionBonoRepository.save(redencion);
            }
        }
        return null;
    }
}