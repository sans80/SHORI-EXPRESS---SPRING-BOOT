package com.shori.shoriexpress.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shori.shoriexpress.model.Rol;
import com.shori.shoriexpress.repository.RolRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles por defecto si no existen
        if (!rolRepository.existsByNombreRol("cliente")) {
            Rol rolCliente = new Rol("cliente");
            rolRepository.save(rolCliente);
            System.out.println("Rol 'cliente' creado");
        }

        if (!rolRepository.existsByNombreRol("admin")) {
            Rol rolAdmin = new Rol("admin");
            rolRepository.save(rolAdmin);
            System.out.println("Rol 'admin' creado");
        }

        if (!rolRepository.existsByNombreRol("empleado")) {
            Rol rolEmpleado = new Rol("empleado");
            rolRepository.save(rolEmpleado);
            System.out.println("Rol 'empleado' creado");
        }
    }
}