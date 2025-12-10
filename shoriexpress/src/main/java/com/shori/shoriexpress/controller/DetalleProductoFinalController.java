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

import com.shori.shoriexpress.model.DetalleProductoFinal;
import com.shori.shoriexpress.model.MateriaPrima;
import com.shori.shoriexpress.model.ProductoTerminado;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.DetalleProductoFinalService;
import com.shori.shoriexpress.service.MateriaPrimaService;
import com.shori.shoriexpress.service.ProductoTerminadoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/detalles-producto-final")
public class DetalleProductoFinalController {

    @Autowired
    private DetalleProductoFinalService detalleProductoFinalService;

    @Autowired
    private MateriaPrimaService materiaPrimaService;

    @Autowired
    private ProductoTerminadoService productoTerminadoService;

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
        
        List<DetalleProductoFinal> detalles = detalleProductoFinalService.listarTodos();
        model.addAttribute("detalles", detalles);
        model.addAttribute("esAdmin", true);
        
        return "detalles-producto-final/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
        List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
        
        model.addAttribute("detalleProductoFinal", new DetalleProductoFinal());
        model.addAttribute("materiasPrimas", materiasPrimas);
        model.addAttribute("productosTerminados", productosTerminados);
        model.addAttribute("unidadesMedida", DetalleProductoFinal.UnidadMedidaDeta.values());
        
        return "detalles-producto-final/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute DetalleProductoFinal detalleProductoFinal,
                              BindingResult result,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("productosTerminados", productosTerminados);
            model.addAttribute("unidadesMedida", DetalleProductoFinal.UnidadMedidaDeta.values());
            return "detalles-producto-final/formulario";
        }
        
        try {
            detalleProductoFinalService.guardar(detalleProductoFinal);
            redirectAttributes.addFlashAttribute("mensaje", "Detalle de producto creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/detalles-producto-final";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el detalle: " + e.getMessage());
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("productosTerminados", productosTerminados);
            model.addAttribute("unidadesMedida", DetalleProductoFinal.UnidadMedidaDeta.values());
            return "detalles-producto-final/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<DetalleProductoFinal> detalleOpt = detalleProductoFinalService.buscarPorId(id);
        if (detalleOpt.isPresent()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            
            model.addAttribute("detalleProductoFinal", detalleOpt.get());
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("productosTerminados", productosTerminados);
            model.addAttribute("unidadesMedida", DetalleProductoFinal.UnidadMedidaDeta.values());
            return "detalles-producto-final/formulario";
        }
        
        return "redirect:/detalles-producto-final";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute DetalleProductoFinal detalleProductoFinal,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("productosTerminados", productosTerminados);
            model.addAttribute("unidadesMedida", DetalleProductoFinal.UnidadMedidaDeta.values());
            return "detalles-producto-final/formulario";
        }
        
        try {
            DetalleProductoFinal detalleActualizado = detalleProductoFinalService.actualizar(id, detalleProductoFinal);
            if (detalleActualizado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Detalle actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Detalle no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el detalle: " + e.getMessage());
        }
        
        return "redirect:/detalles-producto-final";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            detalleProductoFinalService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Detalle eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el detalle: " + e.getMessage());
        }
        
        return "redirect:/detalles-producto-final";
    }
}