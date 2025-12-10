package com.shori.shoriexpress.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.MetodoPago;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.MetodoPagoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && "admin".equals(usuario.getRol().getNombreRol());
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
        model.addAttribute("metodosPago", metodosPago);
        model.addAttribute("esAdmin", true);
        
        return "metodos-pago/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("metodoPago", new MetodoPago());
        return "metodos-pago/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute MetodoPago metodoPago,
                              BindingResult result,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            return "metodos-pago/formulario";
        }
        
        try {
            if (metodoPagoService.existePorNombre(metodoPago.getNombreMetodoPago())) {
                model.addAttribute("error", "Ya existe un método de pago con ese nombre");
                return "metodos-pago/formulario";
            }
            
            metodoPagoService.guardar(metodoPago);
            redirectAttributes.addFlashAttribute("mensaje", "Método de pago creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/metodos-pago";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el método de pago: " + e.getMessage());
            return "metodos-pago/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<MetodoPago> metodoPagoOpt = metodoPagoService.buscarPorId(id);
        if (metodoPagoOpt.isPresent()) {
            model.addAttribute("metodoPago", metodoPagoOpt.get());
            return "metodos-pago/formulario";
        }
        
        return "redirect:/metodos-pago";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute MetodoPago metodoPago,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            return "metodos-pago/formulario";
        }
        
        try {
            MetodoPago metodoPagoActualizado = metodoPagoService.actualizar(id, metodoPago);
            if (metodoPagoActualizado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Método de pago actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Método de pago no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el método de pago: " + e.getMessage());
        }
        
        return "redirect:/metodos-pago";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            metodoPagoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Método de pago eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el método de pago: " + e.getMessage());
        }
        
        return "redirect:/metodos-pago";
    }
}