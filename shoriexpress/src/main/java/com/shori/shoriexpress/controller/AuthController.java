package com.shori.shoriexpress.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.Rol;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.RolService;
import com.shori.shoriexpress.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        // Si ya está logueado, redirigir al dashboard
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", new Usuario());
        return "auth/login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
            @RequestParam String contraseña,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService.autenticar(correo, contraseña);

            if (usuario != null) {
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("nombreCompleto", usuario.getNombreCompleto());
                session.setAttribute("rol", usuario.getRol().getNombreRol());

                redirectAttributes.addFlashAttribute("mensaje", "¡Bienvenido " + usuario.getNombreCompleto() + "!");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");

                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas o usuario inactivo");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error en el sistema. Inténtalo más tarde.");
            return "redirect:/auth/login";
        }
    }

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String procesarRegistro(@RequestParam String nombreUsuario,
            @RequestParam String correoUsuario,
            @RequestParam String contraseñaUsuario,
            @RequestParam String nombreCompleto,
            @RequestParam String documentoUsuario,
            @RequestParam String tipoDocumentoUsuario,
            @RequestParam String telefonoUsuario,
            @RequestParam String direccionUsuario,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // Validaciones básicas
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario es obligatorio");
                return "redirect:/auth/register";
            }

            if (correoUsuario == null || correoUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El correo es obligatorio");
                return "redirect:/auth/register";
            }

            if (contraseñaUsuario == null || contraseñaUsuario.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria");
                return "redirect:/auth/register";
            }

            // Verificar si el correo ya existe
            if (usuarioService.existeCorreo(correoUsuario)) {
                redirectAttributes.addFlashAttribute("error", "El correo ya está registrado");
                return "redirect:/auth/register";
            }

            // Verificar si el nombre de usuario ya existe
            if (usuarioService.existeNombreUsuario(nombreUsuario)) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso");
                return "redirect:/auth/register";
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario.trim());
            nuevoUsuario.setCorreoUsuario(correoUsuario.trim());
            nuevoUsuario.setContraseñaUsuario(contraseñaUsuario);
            nuevoUsuario.setNombreCompleto(nombreCompleto != null ? nombreCompleto.trim() : "");
            nuevoUsuario.setDocumentoUsuario(documentoUsuario != null ? documentoUsuario.trim() : "");
            nuevoUsuario.setTelefonoUsuario(telefonoUsuario != null ? telefonoUsuario.trim() : "");
            nuevoUsuario.setDireccionUsuario(direccionUsuario != null ? direccionUsuario.trim() : "");

            // Asignar tipo de documento
            if (tipoDocumentoUsuario != null && !tipoDocumentoUsuario.isEmpty()) {
                try {
                    nuevoUsuario.setTipoDocumentoUsuario(Usuario.TipoDocumento.valueOf(tipoDocumentoUsuario));
                } catch (IllegalArgumentException e) {
                    nuevoUsuario.setTipoDocumentoUsuario(Usuario.TipoDocumento.CC); // Por defecto CC
                }
            } else {
                nuevoUsuario.setTipoDocumentoUsuario(Usuario.TipoDocumento.CC); // Por defecto CC
            }

            // Asignar rol cliente por defecto
            Optional<Rol> rolCliente = rolService.buscarPorNombre("cliente");
            if (rolCliente.isPresent()) {
                nuevoUsuario.setRol(rolCliente.get());
            } else {
                // Si no existe el rol cliente, crearlo
                Rol nuevoRolCliente = new Rol("cliente");
                rolService.guardar(nuevoRolCliente);
                nuevoUsuario.setRol(nuevoRolCliente);
            }

            // Guardar usuario
            usuarioService.guardar(nuevoUsuario);

            redirectAttributes.addFlashAttribute("mensaje", "¡Registro exitoso! Ya puedes iniciar sesión.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "info");
        return "redirect:/auth/login";
    }

    // Verificar disponibilidad de correo (AJAX)
    @GetMapping("/verificar-correo")
    @ResponseBody
    public boolean verificarCorreo(@RequestParam String correo) {
        try {
            return !usuarioService.existeCorreo(correo);
        } catch (Exception e) {
            return false;
        }
    }

    // Verificar disponibilidad de nombre de usuario (AJAX)
    @GetMapping("/verificar-usuario")
    @ResponseBody
    public boolean verificarUsuario(@RequestParam String nombreUsuario) {
        try {
            return !usuarioService.existeNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            return false;
        }
    }

    // Verificar disponibilidad de documento (AJAX)
    @GetMapping("/verificar-documento")
    @ResponseBody
    public boolean verificarDocumento(@RequestParam String documento) {
        try {
            return !usuarioService.existeDocumento(documento);
        } catch (Exception e) {
            return false;
        }
    }

}