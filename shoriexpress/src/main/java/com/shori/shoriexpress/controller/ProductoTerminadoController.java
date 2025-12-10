package com.shori.shoriexpress.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.ProductoTerminado;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.ProductoTerminadoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoTerminadoController {

    @Autowired
    private ProductoTerminadoService productoTerminadoService;

    // Verificar permisos de admin
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

        List<ProductoTerminado> productos = productoTerminadoService.listarTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("esAdmin", esAdmin(session));

        return "productos/lista";
    }

    // Mostrar formulario de nuevo producto (solo admin)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("producto", new ProductoTerminado());
        return "productos/formulario";
    }

    // Guardar nuevo producto
    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute ProductoTerminado producto,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        if (result.hasErrors()) {
            return "productos/formulario";
        }

        try {
            productoTerminadoService.guardar(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/productos";

        } catch (Exception e) {

            model.addAttribute("error", "Error al crear el producto: " + e.getMessage());

            return "productos/formulario";
        }
    }

    // Mostrar formulario de edición (solo admin)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Optional<ProductoTerminado> productoOpt = productoTerminadoService.buscarPorId(id);
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            return "productos/formulario";
        }

        return "redirect:/productos";
    }

    // Actualizar producto
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
            @Valid @ModelAttribute ProductoTerminado producto,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        if (result.hasErrors()) {
            return "productos/formulario";
        }

        try {
            producto.setIdProductoTerminado(id);
            ProductoTerminado productoActualizado = productoTerminadoService.actualizar(id, producto);

            if (productoActualizado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            }

            return "redirect:/productos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el producto: " + e.getMessage());

            return "productos/formulario";
        }
    }

    // Eliminar producto (solo admin)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        try {
            productoTerminadoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto");
        }

        return "redirect:/productos";
    }

    // Buscar productos
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            HttpSession session,
            Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        List<ProductoTerminado> productos = productoTerminadoService.buscarProductos(nombre, precioMin, precioMax);
        model.addAttribute("productos", productos);
        model.addAttribute("esAdmin", esAdmin(session));
        model.addAttribute("nombre", nombre);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "productos/lista";
    }

    // Ver detalle del producto
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        Optional<ProductoTerminado> productoOpt = productoTerminadoService.buscarPorId(id);
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            return "productos/detalle";
        }

        return "redirect:/productos";
    }
}