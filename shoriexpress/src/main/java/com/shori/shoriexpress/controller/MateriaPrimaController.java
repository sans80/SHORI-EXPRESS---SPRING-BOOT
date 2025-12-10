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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shori.shoriexpress.model.MateriaPrima;
import com.shori.shoriexpress.model.MateriaPrima.EstadoMateriaPrima;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.service.MateriaPrimaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/materias-primas")
public class MateriaPrimaController {

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
        
        List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodas();
        List<String> categorias = materiaPrimaService.listarCategorias();
        
        model.addAttribute("materiasPrimas", materiasPrimas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("esAdmin", esAdmin(session));
        
        return "materias-primas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("materiaPrima", new MateriaPrima());
        model.addAttribute("unidadesMedida", MateriaPrima.UnidadMedida.values());
        model.addAttribute("estados", EstadoMateriaPrima.values());
        return "materias-primas/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute MateriaPrima materiaPrima,
                              BindingResult result,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("unidadesMedida", MateriaPrima.UnidadMedida.values());
            model.addAttribute("estados", EstadoMateriaPrima.values());
            return "materias-primas/formulario";
        }
        
        try {
            materiaPrimaService.guardar(materiaPrima);
            redirectAttributes.addFlashAttribute("mensaje", "Materia prima creada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/materias-primas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la materia prima: " + e.getMessage());
            model.addAttribute("unidadesMedida", MateriaPrima.UnidadMedida.values());
            model.addAttribute("estados", EstadoMateriaPrima.values());
            return "materias-primas/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        Optional<MateriaPrima> materiaPrimaOpt = materiaPrimaService.buscarPorId(id);
        if (materiaPrimaOpt.isPresent()) {
            model.addAttribute("materiaPrima", materiaPrimaOpt.get());
            model.addAttribute("unidadesMedida", MateriaPrima.UnidadMedida.values());
            model.addAttribute("estados", EstadoMateriaPrima.values());
            return "materias-primas/formulario";
        }
        
        return "redirect:/materias-primas";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute MateriaPrima materiaPrima,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("unidadesMedida", MateriaPrima.UnidadMedida.values());
            model.addAttribute("estados", EstadoMateriaPrima.values());
            return "materias-primas/formulario";
        }
        
        try {
            MateriaPrima materiaPrimaActualizada = materiaPrimaService.actualizar(id, materiaPrima);
            if (materiaPrimaActualizada != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Materia prima actualizada exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Materia prima no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la materia prima: " + e.getMessage());
        }
        
        return "redirect:/materias-primas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        try {
            materiaPrimaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Materia prima eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la materia prima: " + e.getMessage());
        }
        
        return "redirect:/materias-primas";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String categoria,
                        @RequestParam(required = false) String estado,
                        HttpSession session,
                        Model model) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }
        
        List<MateriaPrima> materiasPrimas;
        
        if (nombre != null && !nombre.isEmpty()) {
            materiasPrimas = materiaPrimaService.buscarPorNombre(nombre);
        } else if (categoria != null && !categoria.isEmpty()) {
            materiasPrimas = materiaPrimaService.buscarPorCategoria(categoria);
        } else if (estado != null && !estado.isEmpty()) {
            materiasPrimas = materiaPrimaService.buscarPorEstado(EstadoMateriaPrima.valueOf(estado));
        } else {
            materiasPrimas = materiaPrimaService.listarTodas();
        }
        
        List<String> categorias = materiaPrimaService.listarCategorias();
        
        model.addAttribute("materiasPrimas", materiasPrimas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("esAdmin", esAdmin(session));
        model.addAttribute("nombre", nombre);
        model.addAttribute("categoria", categoria);
        model.addAttribute("estado", estado);
        
        return "materias-primas/lista";
    }

    @GetMapping("/stock-bajo")
    public String verStockBajo(@RequestParam(defaultValue = "10") Integer minStock,
                              HttpSession session,
                              Model model) {
        
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        List<MateriaPrima> materiasPrimas = materiaPrimaService.buscarStockBajo(minStock);
        model.addAttribute("materiasPrimas", materiasPrimas);
        model.addAttribute("esAdmin", true);
        model.addAttribute("stockBajo", true);
        
        return "materias-primas/lista";
    }
}