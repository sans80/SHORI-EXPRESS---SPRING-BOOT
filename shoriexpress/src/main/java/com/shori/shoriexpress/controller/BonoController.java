package com.shori.shoriexpress.controller;

import java.util.Collection;
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

import com.shori.shoriexpress.model.Bono;
import com.shori.shoriexpress.model.Bono.EstadoBono;
import com.shori.shoriexpress.model.Usuario;
import com.shori.shoriexpress.repository.BonoRepository;
import com.shori.shoriexpress.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/bonos")
public class BonoController {

    @Autowired
    private BonoRepository bonoRepository;

    @Autowired
    private UsuarioService usuarioService;

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

        List<Bono> bonos;
        if (esAdmin(session)) {
            bonos = bonoRepository.findAll();
        } else {
            bonos = (List<Bono>) bonoRepository.findByUsuario_IdUsuario(usuarioLogueado.getIdUsuario());
        }

        model.addAttribute("bonos", bonos);
        model.addAttribute("esAdmin", esAdmin(session));

        return "bonos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        List<Usuario> usuarios = usuarioService.listarTodos();

        model.addAttribute("bono", new Bono());
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("estados", EstadoBono.values());

        return "bonos/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@Valid @ModelAttribute Bono bono,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Long idUsuario = bono.getUsuario().getIdUsuario();

            Usuario usuarioCompleto = usuarioService.obtenerPorId(idUsuario);

            if (usuarioCompleto == null) {
                throw new Exception("El usuario seleccionado no es válido o no existe.");
            }

            bono.setUsuario(usuarioCompleto);

            // 4. Guardar el Bono
            bonoRepository.save(bono);

            redirectAttributes.addFlashAttribute("mensaje", "Bono creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/bonos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el bono: " + e.getMessage());
            List<Usuario> usuarios = usuarioService.listarTodos();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("estados", Bono.EstadoBono.values());
            return "bonos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, HttpSession session, Model model) {

        @SuppressWarnings("null")
        Optional<Bono> bonoOpt = bonoRepository.findById(id);
        if (bonoOpt.isPresent()) {
            List<Usuario> usuarios = usuarioService.listarTodos();

            model.addAttribute("bono", bonoOpt.get());
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("estados", EstadoBono.values());
            return "bonos/formulario";
        }

        return "redirect:/bonos";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
            @Valid @ModelAttribute Bono bono,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        if (result.hasErrors()) {
            List<Usuario> usuarios = usuarioService.listarTodos();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("estados", EstadoBono.values());
            return "bonos/formulario";
        }

        try {
            Optional<Bono> existente = bonoRepository.findById(id);
            if (existente.isPresent()) {
                Bono bonoActualizado = existente.get();
                bonoActualizado.setPuntosAcumuladosBono(bono.getPuntosAcumuladosBono());
                bonoActualizado.setPuntosNecesariosBono(bono.getPuntosNecesariosBono());
                bonoActualizado.setEstadoBono(bono.getEstadoBono());
                bonoActualizado.setUsuario(bono.getUsuario());
                bonoRepository.save(bonoActualizado);

                redirectAttributes.addFlashAttribute("mensaje", "Bono actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bono no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el bono: " + e.getMessage());
        }

        return "redirect:/bonos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        try {
            bonoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Bono eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el bono: " + e.getMessage());
        }

        return "redirect:/bonos";
    }

    @GetMapping("/mi-bono")
    public String verMiBono(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/auth/login";
        }

        Optional<Bono> bonoOpt = ((Collection<Bono>) bonoRepository
                .findByUsuario_IdUsuario(usuarioLogueado.getIdUsuario())).stream().findFirst();

        if (bonoOpt.isPresent()) {
            model.addAttribute("bono", bonoOpt.get());
        } else {
            model.addAttribute("mensaje", "No tienes bonos disponibles");
        }

        return "bonos/mi-bono";
    }
}