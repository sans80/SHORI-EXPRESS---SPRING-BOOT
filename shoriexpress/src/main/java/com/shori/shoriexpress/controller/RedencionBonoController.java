package com.shori.shoriexpress.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.Bono;
import com.shori.shoriexpress.model.RedencionBono;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.repository.BonoRepository;
import com.shori.shoriexpress.service.RedencionBonoService;
import com.shori.shoriexpress.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/redenciones")
public class RedencionBonoController {

    @Autowired
    private RedencionBonoService redencionBonoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private BonoRepository bonoRepository;

    // Verificar permisos de admin
    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && "admin".equals(usuario.getRol().getNombreRol());
    }

    // Listar redenciones
    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        List<RedencionBono> redenciones;
        if (esAdmin(session)) {
            redenciones = redencionBonoService.listarTodas();
        } else {
            redenciones = redencionBonoService.buscarPorUsuario(usuarioLogueado);
        }
        
        model.addAttribute("redenciones", redenciones);
        model.addAttribute("esAdmin", esAdmin(session));
        
        return "redenciones/lista";
    }

    // Mostrar formulario de nueva redención (solo admin)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Bono> bonosDisponibles = bonoRepository.findByEstadoBono(Bono.EstadoBono.disponible);
        
        model.addAttribute("redencion", new RedencionBono());
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("bonos", bonosDisponibles);
        
        return "redenciones/formulario";
    }

    // Redimir bono
    @PostMapping("/redimir")
    public String redimirBono(@RequestParam Long idBono,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        try {
            RedencionBono redencion = redencionBonoService.redimirBono(idBono, usuarioLogueado);
            if (redencion != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Bono redimido exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo redimir el bono");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al redimir el bono");
        }
        
        return "redirect:/redenciones";
    }

    // Eliminar redención (solo admin)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            redencionBonoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Redención eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la redención");
        }
        
        return "redirect:/redenciones";
    }

    // Buscar redenciones
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) Long usuarioId,
                        @RequestParam(required = false) String fechaInicio,
                        @RequestParam(required = false) String fechaFin,
                        HttpSession session,
                        Model model) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioService.buscarPorId(usuarioId).orElse(null);
        }
        
        LocalDateTime fechaInicioDate = null;
        LocalDateTime fechaFinDate = null;
        
        try {
            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                fechaInicioDate = LocalDateTime.parse(fechaInicio + "T00:00:00");
            }
            if (fechaFin != null && !fechaFin.isEmpty()) {
                fechaFinDate = LocalDateTime.parse(fechaFin + "T23:59:59");
            }
        } catch (Exception e) {
            // Fechas inválidas, usar null
        }
        
        List<RedencionBono> redenciones = redencionBonoService.buscarRedenciones(usuario, fechaInicioDate, fechaFinDate);
        List<Usuario> usuarios = usuarioService.listarTodos();
        
        model.addAttribute("redenciones", redenciones);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("esAdmin", true);
        
        return "redenciones/lista";
    }
}