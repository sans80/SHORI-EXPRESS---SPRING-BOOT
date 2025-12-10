package com.shori.shoriexpress.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.RolService;
import com.shori.shoriexpress.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    // Listar usuarios (solo para admin)
    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        if (!"admin".equals(usuarioLogueado.getRol().getNombreRol())) {
            return "redirect:/dashboard";
        }

        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    // Mostrar perfil del usuario logueado
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Usuario usuarioActualizado = usuarioService.buscarPorId(usuario.getIdUsuario()).orElse(usuario);
        model.addAttribute("usuario", usuarioActualizado);

        return "usuarios/perfil";
    }

    // Mostrar formulario de edición de perfil
    @GetMapping("/editar-perfil")
    public String mostrarEditarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/auth/login";
        }
        Usuario usuarioActualizado = usuarioService.buscarPorId(usuario.getIdUsuario()).orElse(usuario);
        model.addAttribute("usuario", usuarioActualizado);

        model.addAttribute("tiposDocumento", Usuario.TipoDocumento.values());

        return "usuarios/editar-perfil";
    }

    @PostMapping("/editar-perfil")
    public String procesarEditarPerfil(@RequestParam String primerNombreUsuario,
            @RequestParam String apellidoUsuario,
            @RequestParam(required = false) String telefonoUsuario,
            @RequestParam String direccionUsuario,
            @RequestParam String tipoDocumentoUsuario,
            @RequestParam String documentoUsuario,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        try {
            // Validaciones básicas
            if (primerNombreUsuario == null || primerNombreUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El primer nombre es obligatorio");
                return "redirect:/usuarios/editar-perfil";
            }

            if (apellidoUsuario == null || apellidoUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El apellido es obligatorio");
                return "redirect:/usuarios/editar-perfil";
            }

            if (direccionUsuario == null || direccionUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La dirección es obligatoria");
                return "redirect:/usuarios/editar-perfil";
            }

            if (documentoUsuario == null || documentoUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El documento es obligatorio");
                return "redirect:/usuarios/editar-perfil";
            }

            if (tipoDocumentoUsuario == null || tipoDocumentoUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El tipo de documento es obligatorio");
                return "redirect:/usuarios/editar-perfil";
            }

            // Validar que el documento no esté en uso por otro usuario
            Usuario usuarioConDocumento = usuarioService.buscarPorDocumento(documentoUsuario.trim());
            if (usuarioConDocumento != null &&
                    !usuarioConDocumento.getIdUsuario().equals(usuarioLogueado.getIdUsuario())) {
                redirectAttributes.addFlashAttribute("error", "El documento ya está registrado por otro usuario");
                return "redirect:/usuarios/editar-perfil";
            }

            // Obtener usuario actual de la base de datos
            Usuario usuarioActual = usuarioService.buscarPorId(usuarioLogueado.getIdUsuario()).orElse(null);

            if (usuarioActual == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

            // Actualizar solo los campos permitidos
            usuarioActual.setPrimerNombreUsuario(primerNombreUsuario.trim());
            usuarioActual.setApellidoUsuario(apellidoUsuario.trim());
            usuarioActual.setTelefonoUsuario(telefonoUsuario != null ? telefonoUsuario.trim() : "");
            usuarioActual.setDireccionUsuario(direccionUsuario.trim());
            usuarioActual.setDocumentoUsuario(documentoUsuario.trim());

            // Actualizar tipo de documento
            try {
                usuarioActual.setTipoDocumentoUsuario(Usuario.TipoDocumento.valueOf(tipoDocumentoUsuario));
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Tipo de documento inválido");
                return "redirect:/usuarios/editar-perfil";
            }

            // Guardar cambios usando el método correcto
            Usuario usuarioActualizado = usuarioService.actualizarPerfilCompleto(usuarioActual);

            if (usuarioActualizado != null) {
                // Actualizar sesión con los nuevos datos
                session.setAttribute("usuarioLogueado", usuarioActualizado);
                session.setAttribute("nombreCompleto", usuarioActualizado.getNombreCompleto());

                redirectAttributes.addFlashAttribute("mensaje", "✓ Perfil actualizado correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil");
                return "redirect:/usuarios/editar-perfil";
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error en el sistema: " + e.getMessage());
            return "redirect:/usuarios/editar-perfil";
        }

        return "redirect:/usuarios/perfil";
    }

    // Mostrar formulario de cambio de contraseña
    @GetMapping("/cambiar-contraseña")
    public String mostrarCambiarContraseña(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        return "usuarios/cambiar-contraseña";
    }

    @PostMapping("/cambiar-contraseña")
    public String procesarCambiarContraseña(@RequestParam String contraseñaActual,
            @RequestParam String nuevaContraseña,
            @RequestParam String confirmarContraseña,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        // Validaciones
        if (contraseñaActual == null || contraseñaActual.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual es obligatoria");
            return "redirect:/usuarios/cambiar-contraseña";
        }

        if (nuevaContraseña == null || nuevaContraseña.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña es obligatoria");
            return "redirect:/usuarios/cambiar-contraseña";
        }

        if (!nuevaContraseña.equals(confirmarContraseña)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/usuarios/cambiar-contraseña";
        }

        if (nuevaContraseña.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres");
            return "redirect:/usuarios/cambiar-contraseña";
        }

        try {
            boolean cambioExitoso = usuarioService.cambiarContraseña(
                    usuarioLogueado.getIdUsuario(),
                    contraseñaActual,
                    nuevaContraseña);

            if (cambioExitoso) {
                redirectAttributes.addFlashAttribute("mensaje", "Contraseña cambiada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error en el sistema: " + e.getMessage());
        }

        return "redirect:/usuarios/perfil";
    }

    // Buscar usuarios
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String documento,
            HttpSession session,
            Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        if (!"admin".equals(usuarioLogueado.getRol().getNombreRol())) {
            return "redirect:/dashboard";
        }

        List<Usuario> usuarios = usuarioService.buscarUsuarios(nombre, correo, documento);
        model.addAttribute("usuarios", usuarios);

        return "usuarios/lista";
    }
}
