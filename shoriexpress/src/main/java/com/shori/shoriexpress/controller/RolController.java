package com.shori.shoriexpress.controller;

import com.shori.shoriexpress.model.Rol;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.RolService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // Verificar permisos de admin
    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && "admin".equals(usuario.getRol().getNombreRol());
    }

    // Listar roles (solo admin)
    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<Rol> roles = rolService.listarTodos();
        model.addAttribute("roles", roles);
        
        return "roles/lista";
    }

    // Mostrar formulario de nuevo rol (solo admin)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("rol", new Rol());
        return "roles/formulario";
    }

    // Guardar nuevo rol
    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute Rol rol,
                              BindingResult result,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        // Verificar si ya existe un rol con ese nombre
        if (rolService.buscarPorNombre(rol.getNombreRol()).isPresent()) {
            result.rejectValue("nombreRol", "error.rol", "Ya existe un rol con ese nombre");
        }
        
        if (result.hasErrors()) {
            return "roles/formulario";
        }
        
        try {
            rolService.guardar(rol);
            redirectAttributes.addFlashAttribute("mensaje", "Rol creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/roles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el rol");
            return "roles/formulario";
        }
    }

    // Mostrar formulario de edición (solo admin)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<Rol> rolOpt = rolService.buscarPorId(id);
        if (rolOpt.isPresent()) {
            model.addAttribute("rol", rolOpt.get());
            return "roles/formulario";
        }
        
        return "redirect:/roles";
    }

    // Actualizar rol
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute Rol rol,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        // Verificar si ya existe otro rol con ese nombre
        Optional<Rol> rolExistente = rolService.buscarPorNombre(rol.getNombreRol());
        if (rolExistente.isPresent() && !rolExistente.get().getIdRol().equals(id)) {
            result.rejectValue("nombreRol", "error.rol", "Ya existe un rol con ese nombre");
        }
        
        if (result.hasErrors()) {
            return "roles/formulario";
        }
        
        try {
            Rol rolActualizado = rolService.actualizar(id, rol);
            if (rolActualizado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Rol actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol");
        }
        
        return "redirect:/roles";
    }

    // Eliminar rol (solo admin)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            // Verificar que no sea un rol crítico
            Optional<Rol> rolOpt = rolService.buscarPorId(id);
            if (rolOpt.isPresent()) {
                String nombreRol = rolOpt.get().getNombreRol().toLowerCase();
                if ("admin".equals(nombreRol) || "cliente".equals(nombreRol)) {
                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar el rol " + nombreRol + " (rol del sistema)");
                    return "redirect:/roles";
                }
            }
            
            rolService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Rol eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el rol. Puede que tenga usuarios asociados.");
        }
        
        return "redirect:/roles";
    }
}