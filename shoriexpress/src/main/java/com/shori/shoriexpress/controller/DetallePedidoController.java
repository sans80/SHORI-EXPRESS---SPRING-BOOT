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

import com.shori.shoriexpress.model.DetallePedido;
import com.shori.shoriexpress.model.Pedido;
import com.shori.shoriexpress.model.ProductoTerminado;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.DetallePedidoService;
import com.shori.shoriexpress.service.PedidoService;
import com.shori.shoriexpress.service.ProductoTerminadoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/detalles-pedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @Autowired
    private PedidoService pedidoService;

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
        
        List<DetallePedido> detalles = detallePedidoService.listarTodos();
        model.addAttribute("detalles", detalles);
        model.addAttribute("esAdmin", esAdmin(session));
        
        return "detalles-pedido/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
        
        model.addAttribute("detallePedido", new DetallePedido());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("productosTerminados", productosTerminados);
        
        return "detalles-pedido/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute DetallePedido detallePedido,
                              BindingResult result,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("productosTerminados", productosTerminados);
            return "detalles-pedido/formulario";
        }
        
        try {
            detallePedidoService.guardar(detallePedido);
            redirectAttributes.addFlashAttribute("mensaje", "Detalle de pedido creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/detalles-pedido";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el detalle: " + e.getMessage());
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("productosTerminados", productosTerminados);
            return "detalles-pedido/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        Optional<DetallePedido> detalleOpt = detallePedidoService.buscarPorId(id);
        if (detalleOpt.isPresent()) {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            
            model.addAttribute("detallePedido", detalleOpt.get());
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("productosTerminados", productosTerminados);
            return "detalles-pedido/formulario";
        }
        
        return "redirect:/detalles-pedido";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute DetallePedido detallePedido,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<ProductoTerminado> productosTerminados = productoTerminadoService.listarTodos();
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("productosTerminados", productosTerminados);
            return "detalles-pedido/formulario";
        }
        
        try {
            DetallePedido detalleActualizado = detallePedidoService.actualizar(id, detallePedido);
            if (detalleActualizado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Detalle actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Detalle no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el detalle: " + e.getMessage());
        }
        
        return "redirect:/detalles-pedido";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/detalles-pedido";
        }
        
        try {
            detallePedidoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Detalle eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el detalle: " + e.getMessage());
        }
        
        return "redirect:/detalles-pedido";
    }
}