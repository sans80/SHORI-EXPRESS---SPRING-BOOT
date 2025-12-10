package com.shori.shoriexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shori.shoriexpress.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreoUsuario(String correo);
    
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    Optional<Usuario> findByDocumentoUsuario(String documento);
    
    boolean existsByCorreoUsuario(String correo);
    
    boolean existsByNombreUsuario(String nombreUsuario);
    
    boolean existsByDocumentoUsuario(String documento);
    
    List<Usuario> findByEstadoUsuario(Usuario.EstadoUsuario estado);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol = :nombreRol")
    List<Usuario> findByRolNombre(@Param("nombreRol") String nombreRol);
    
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nombre IS NULL OR u.primerNombreUsuario LIKE %:nombre% OR u.apellidoUsuario LIKE %:nombre%) AND " +
           "(:correo IS NULL OR u.correoUsuario LIKE %:correo%) AND " +
           "(:documento IS NULL OR u.documentoUsuario LIKE %:documento%)")
    List<Usuario> buscarUsuarios(@Param("nombre") String nombre, 
                               @Param("correo") String correo, 
                               @Param("documento") String documento);
}