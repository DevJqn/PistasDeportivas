// controller/web/UsuarioWebController.java
package com.joaquin.pistasdeportivas.controller.web;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.Rol;
import com.joaquin.pistasdeportivas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioWebController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/editar/{id}")
public String editarForm(@PathVariable Long id, Model model) {
    UsuarioResponseDTO u = usuarioService.buscarPorId(id);

    // Aquí solo pasamos el rol como String
    String rol = u.getRol();

    model.addAttribute("dto", new UsuarioUpdateDTO(
            u.getNombre(),
            u.getTelefono(),
            u.getEmail(),
            "", // password vacío
            rol
    ));

    model.addAttribute("usuarioId", id);
    model.addAttribute("accion", "Editar");
    model.addAttribute("roles", List.of("USER", "ADMIN")); // Strings para el select
    return "usuarios/formulario";
}

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("dto") UsuarioUpdateDTO dto,
                             BindingResult result, Model model,
                             RedirectAttributes attrs) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", id);
            return "usuarios/formulario";
        }
        usuarioService.actualizar(id, dto);
        attrs.addFlashAttribute("exito", "Usuario actualizado");
        return "redirect:/web/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        usuarioService.eliminar(id);
        attrs.addFlashAttribute("exito", "Usuario eliminado");
        return "redirect:/web/usuarios";
    }
}
