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

import com.shori.shoriexpress.model.EntradaInventario;
import com.shori.shoriexpress.model.EntradaInventario.TipoEntrada;
import com.shori.shoriexpress.model.MateriaPrima;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.EntradaInventarioService;
import com.shori.shoriexpress.service.MateriaPrimaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/entradas-inventario")
public class EntradaInventarioController {

    @Autowired
    private EntradaInventarioService entradaInventarioService;

    @Autowired
    private MateriaPrimaService materiaPrimaService;

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

        List<EntradaInventario> entradas = entradaInventarioService.listarTodas();
        model.addAttribute("entradas", entradas);
        model.addAttribute("esAdmin", true);

        return "entradas-inventario/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();

        model.addAttribute("entradaInventario", new EntradaInventario());
        model.addAttribute("materiasPrimas", materiasPrimas);
        model.addAttribute("tiposEntrada", TipoEntrada.values());

        return "entradas-inventario/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute EntradaInventario entradaInventario,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        entradaInventario.setUsuario(usuarioLogueado);

        if (result.hasErrors()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("tiposEntrada", EntradaInventario.TipoEntrada.values());
            return "entradas-inventario/formulario";
        }

        try {
            Long idMateriaPrima = entradaInventario.getMateriaPrima().getIdMateriaPrima();

            Optional<MateriaPrima> materiaOpt = materiaPrimaService.buscarPorId(idMateriaPrima);

            if (materiaOpt.isEmpty()) {
                throw new IllegalArgumentException("La Materia Prima seleccionada no existe.");
            }

            entradaInventario.setMateriaPrima(materiaOpt.get());

            entradaInventarioService.guardar(entradaInventario);
            redirectAttributes.addFlashAttribute("mensaje", "Entrada de inventario registrada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/entradas-inventario";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar la entrada: " + e.getMessage());
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("tiposEntrada", EntradaInventario.TipoEntrada.values());
            model.addAttribute("entradaInventario", entradaInventario);
            return "entradas-inventario/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Optional<EntradaInventario> entradaOpt = entradaInventarioService.buscarPorId(id);
        if (entradaOpt.isPresent()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();

            model.addAttribute("entradaInventario", entradaOpt.get());
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("tiposEntrada", TipoEntrada.values());
            return "entradas-inventario/formulario";
        }

        return "redirect:/entradas-inventario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
            @ModelAttribute EntradaInventario entradaInventario,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        entradaInventario.setUsuario(usuarioLogueado);

        if (result.hasErrors()) {
            List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
            model.addAttribute("materiasPrimas", materiasPrimas);
            model.addAttribute("tiposEntrada", EntradaInventario.TipoEntrada.values());
            return "entradas-inventario/formulario";
        }

        try {
            Long idMateriaPrima = entradaInventario.getMateriaPrima().getIdMateriaPrima();
            Optional<MateriaPrima> materiaOpt = materiaPrimaService.buscarPorId(idMateriaPrima);

            if (materiaOpt.isEmpty()) {
                throw new IllegalArgumentException("La Materia Prima seleccionada no existe.");
            }

            entradaInventario.setMateriaPrima(materiaOpt.get());

            entradaInventarioService.actualizar(id, entradaInventario);

            redirectAttributes.addFlashAttribute("mensaje", "Entrada actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la entrada: " + e.getMessage());
        }

        return "redirect:/entradas-inventario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        try {
            entradaInventarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Entrada eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la entrada: " + e.getMessage());
        }

        return "redirect:/entradas-inventario";
    }
}