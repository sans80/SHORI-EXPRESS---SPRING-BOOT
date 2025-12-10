package com.shori.shoriexpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.Rol;
import com.shori.shoriexpress.repository.RolRepository;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombreRol(nombre);
    }

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol actualizar(Long id, Rol datosActualizados) {
        Optional<Rol> rolOpt = rolRepository.findById(id);
        if (rolOpt.isPresent()) {
            Rol rol = rolOpt.get();
            rol.setNombreRol(datosActualizados.getNombreRol());
            return rolRepository.save(rol);
        }
        return null;
    }

    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }

    public boolean existeNombre(String nombre) {
        return rolRepository.findByNombreRol(nombre).isPresent();
    }
}