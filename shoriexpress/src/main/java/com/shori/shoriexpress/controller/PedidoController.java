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

import com.shori.shoriexpress.model.Pedido;
import com.shori.shoriexpress.model.Pedido.EstadoPedido;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.PedidoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

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

        List<Pedido> pedidos;
        if (esAdmin(session)) {
            pedidos = pedidoService.listarTodos();
        } else {
            pedidos = pedidoService.buscarPorUsuario(usuarioLogueado.getIdUsuario());
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("esAdmin", esAdmin(session));

        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("pedido", new Pedido());
        model.addAttribute("estados", EstadoPedido.values());
        return "pedidos/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute Pedido pedido,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        pedido.setUsuario(usuarioLogueado);

        if (result.hasErrors()) {
            model.addAttribute("estados", EstadoPedido.values());
            return "pedidos/formulario";
        }

        try {
            pedidoService.guardar(pedido);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/pedidos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el pedido: " + e.getMessage());
            model.addAttribute("estados", EstadoPedido.values());

            model.addAttribute("pedido", pedido);
            return "pedidos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();

            if (!esAdmin(session) && !pedido.getUsuario().getIdUsuario().equals(usuarioLogueado.getIdUsuario())) {
                return "redirect:/pedidos";
            }

            model.addAttribute("pedido", pedido);
            model.addAttribute("estados", EstadoPedido.values());
            return "pedidos/formulario";
        }

        return "redirect:/pedidos";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
            @ModelAttribute Pedido pedido,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Pedido> pedidoOriginalOpt = pedidoService.buscarPorId(id);
        if (pedidoOriginalOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado para actualizar.");
            return "redirect:/pedidos";
        }

        Pedido pedidoOriginal = pedidoOriginalOpt.get();

        pedido.setUsuario(pedidoOriginal.getUsuario());

        if (result.hasErrors()) {
            model.addAttribute("estados", EstadoPedido.values());
            return "pedidos/formulario";
        }

        try {

            Pedido pedidoActualizado = pedidoService.actualizar(id, pedido);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pedido: " + e.getMessage());
        }

        return "redirect:/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/pedidos";
        }

        try {
            pedidoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el pedido: " + e.getMessage());
        }

        return "redirect:/pedidos";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
            @ModelAttribute("nuevoEstado") String nuevoEstado,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            return "redirect:/pedidos";
        }

        try {
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);
            pedidoService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }

        return "redirect:/pedidos";
    }
}