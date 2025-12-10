package com.shori.shoriexpress.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.Rol;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.repository.RolRepository;
import com.shori.shoriexpress.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;

    public Usuario obtenerPorId(Long id) {
    // Asegúrate de que usa el repositorio y maneja el Optional
    return usuarioRepository.findById(id).orElse(null);
}

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoUsuario(correo);
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public Usuario buscarPorDocumento(String documento) {
        Optional<Usuario> usuario = usuarioRepository.findByDocumentoUsuario(documento);
        return usuario.orElse(null);
    }

    public Usuario guardar(Usuario usuario) {
        // Encriptar contraseña antes de guardar
        if (usuario.getContraseñaUsuario() != null && !usuario.getContraseñaUsuario().isEmpty()) {
            usuario.setContraseñaUsuario(encriptarContraseña(usuario.getContraseñaUsuario()));
        }
        
        // Asignar rol por defecto si no tiene
        if (usuario.getRol() == null) {
            Optional<Rol> rolCliente = rolRepository.findByNombreRol("cliente");
            if (rolCliente.isPresent()) {
                usuario.setRol(rolCliente.get());
            }
        }
        
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreoUsuario(correo);
    }

    public boolean existeNombreUsuario(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    public boolean existeDocumento(String documento) {
        return usuarioRepository.existsByDocumentoUsuario(documento);
    }

    public Usuario autenticar(String correo, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoUsuario(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String contraseñaEncriptada = encriptarContraseña(contraseña);
            
            if (usuario.getContraseñaUsuario().equals(contraseñaEncriptada) && 
                usuario.getEstadoUsuario() == Usuario.EstadoUsuario.activo) {
                return usuario;
            }
        }
        
        return null;
    }

    public List<Usuario> buscarUsuarios(String nombre, String correo, String documento) {
        return usuarioRepository.buscarUsuarios(nombre, correo, documento);
    }

    public List<Usuario> listarPorEstado(Usuario.EstadoUsuario estado) {
        return usuarioRepository.findByEstadoUsuario(estado);
    }

    public List<Usuario> listarPorRol(String nombreRol) {
        return usuarioRepository.findByRolNombre(nombreRol);
    }

    public Usuario cambiarEstado(Long id, Usuario.EstadoUsuario nuevoEstado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEstadoUsuario(nuevoEstado);
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // MÉTODO ANTIGUO - Mantener para compatibilidad
    public Usuario actualizarPerfil(Long id, Usuario datosActualizados) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            usuario.setPrimerNombreUsuario(datosActualizados.getPrimerNombreUsuario());
            usuario.setApellidoUsuario(datosActualizados.getApellidoUsuario());
            usuario.setTelefonoUsuario(datosActualizados.getTelefonoUsuario());
            usuario.setDireccionUsuario(datosActualizados.getDireccionUsuario());
            
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // NUEVO MÉTODO - Actualización completa del perfil
    @Transactional
    public Usuario actualizarPerfilCompleto(Usuario usuarioActualizado) {
        try {
            // Verificar que el usuario existe
            if (usuarioActualizado.getIdUsuario() == null) {
                throw new RuntimeException("El ID del usuario no puede ser nulo");
            }
            
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuarioActualizado.getIdUsuario());
            if (!usuarioExistente.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            // Guardar los cambios
            Usuario usuarioGuardado = usuarioRepository.save(usuarioActualizado);
            
            // Forzar la actualización en la base de datos
            usuarioRepository.flush();
            
            return usuarioGuardado;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el perfil: " + e.getMessage(), e);
        }
    }

    public boolean cambiarContraseña(Long id, String contraseñaActual, String nuevaContraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String contraseñaActualEncriptada = encriptarContraseña(contraseñaActual);
            
            if (usuario.getContraseñaUsuario().equals(contraseñaActualEncriptada)) {
                usuario.setContraseñaUsuario(encriptarContraseña(nuevaContraseña));
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }

    private String encriptarContraseña(String contraseña) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contraseña.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
}   