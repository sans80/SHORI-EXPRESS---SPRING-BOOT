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

import com.shori.shoriexpress.model.Factura;
import com.shori.shoriexpress.model.MetodoPago;
import com.shori.shoriexpress.model.Pedido;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.FacturaService;
import com.shori.shoriexpress.service.MetodoPagoService;
import com.shori.shoriexpress.service.PedidoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PedidoService pedidoService;

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
        
        List<Factura> facturas = facturaService.listarTodas();
        model.addAttribute("facturas", facturas);
        model.addAttribute("esAdmin", true);
        
        return "facturas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
        
        model.addAttribute("factura", new Factura());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("metodosPago", metodosPago);
        
        return "facturas/formulario";
    }

@PostMapping("/nuevo")
public String guardarNuevo(@Valid @ModelAttribute Factura factura,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {

    if (!esAdmin(session)) {
        return "redirect:/dashboard";
    }

    if (result.hasErrors()) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("metodosPago", metodosPago);
        return "facturas/formulario";
    }

    try {

        Long idPedido = factura.getPedido().getIdPedido();
        Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("El Pedido seleccionado no existe.");
        }
        factura.setPedido(pedidoOpt.get());

        if (factura.getMetodoPago() != null && factura.getMetodoPago().getIdMetodoPago() != null) {
            Long idMetodoPago = factura.getMetodoPago().getIdMetodoPago();
            Optional<MetodoPago> metodoOpt = metodoPagoService.buscarPorId(idMetodoPago);
            
            if (metodoOpt.isEmpty()) {
                throw new IllegalArgumentException("El Método de Pago seleccionado no existe.");
            }
            factura.setMetodoPago(metodoOpt.get());
        }
        
        facturaService.guardar(factura);
        redirectAttributes.addFlashAttribute("mensaje", "Factura creada exitosamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/facturas";
        
    } catch (Exception e) {
        model.addAttribute("error", "Error al crear la factura: " + e.getMessage());
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("metodosPago", metodosPago);
        return "facturas/formulario";
    }
}

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<Factura> facturaOpt = facturaService.buscarPorId(id);
        if (facturaOpt.isPresent()) {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
            
            model.addAttribute("factura", facturaOpt.get());
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("metodosPago", metodosPago);
            return "facturas/formulario";
        }
        
        return "redirect:/facturas";
    }

@PostMapping("/editar/{id}")
public String actualizar(@PathVariable Long id,
                         @Valid @ModelAttribute Factura factura,
                         BindingResult result,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
    
    if (!esAdmin(session)) {
        return "redirect:/dashboard";
    }
    
    if (result.hasErrors()) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<MetodoPago> metodosPago = metodoPagoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("metodosPago", metodosPago);
        return "facturas/formulario";
    }
    
    try {

        Long idPedido = factura.getPedido().getIdPedido();
        Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("El Pedido seleccionado no existe.");
        }
        factura.setPedido(pedidoOpt.get());

        if (factura.getMetodoPago() != null && factura.getMetodoPago().getIdMetodoPago() != null) {
            Long idMetodoPago = factura.getMetodoPago().getIdMetodoPago();
            Optional<MetodoPago> metodoOpt = metodoPagoService.buscarPorId(idMetodoPago);
            
            if (metodoOpt.isEmpty()) {
                throw new IllegalArgumentException("El Método de Pago seleccionado no existe.");
            }
            factura.setMetodoPago(metodoOpt.get());
        }
        
        Factura facturaActualizada = facturaService.actualizar(id, factura);
        if (facturaActualizada != null) {
            redirectAttributes.addFlashAttribute("mensaje", "Factura actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
        }
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar la factura: " + e.getMessage());
    }
    
    return "redirect:/facturas";
}

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            facturaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Factura eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la factura: " + e.getMessage());
        }
        
        return "redirect:/facturas";
    }

    @GetMapping("/ver/{id}")
    public String verDetalle(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<Factura> facturaOpt = facturaService.buscarPorId(id);
        if (facturaOpt.isPresent()) {
            model.addAttribute("factura", facturaOpt.get());
            return "facturas/detalle";
        }
        
        return "redirect:/facturas";
    }
}