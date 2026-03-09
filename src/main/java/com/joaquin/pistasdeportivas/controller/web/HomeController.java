// controller/web/HomeController.java
package com.joaquin.pistasdeportivas.controller.web;

import com.joaquin.pistasdeportivas.dto.UsuarioRegistroDTO;
import com.joaquin.pistasdeportivas.exception.ReservaException;
import com.joaquin.pistasdeportivas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("dto", new UsuarioRegistroDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("dto") UsuarioRegistroDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) return "registro";
        try {
            usuarioService.registrar(dto);
            return "redirect:/login?registrado";
        } catch (ReservaException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @GetMapping("/web/home")
    public String home() { return "home"; }
}
