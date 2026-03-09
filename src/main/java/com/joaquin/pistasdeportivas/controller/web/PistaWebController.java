package com.joaquin.pistasdeportivas.controller.web;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.TipoPista;
import com.joaquin.pistasdeportivas.exception.ReservaException;
import com.joaquin.pistasdeportivas.service.PistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/pistas")
public class PistaWebController {

    @Autowired private PistaService pistaService;

    @GetMapping
    public String listar(@RequestParam(required = false) String nombre, Model model) {
        model.addAttribute("pistas",
                nombre != null && !nombre.isBlank()
                        ? pistaService.buscarPorNombre(nombre)
                        : pistaService.listarTodas());
        model.addAttribute("busqueda", nombre);
        return "pistas/lista";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevaForm(Model model) {
        model.addAttribute("dto", new PistaRequestDTO());
        model.addAttribute("tipos", TipoPista.values());
        model.addAttribute("accion", "Nueva");
        return "pistas/formulario";
    }

    @PostMapping("/nueva")
    @PreAuthorize("hasRole('ADMIN')")
    public String crear(@Valid @ModelAttribute("dto") PistaRequestDTO dto,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoPista.values());
            model.addAttribute("accion", "Nueva");
            return "pistas/formulario";
        }
        pistaService.crear(dto);
        return "redirect:/web/pistas?exito=Pista creada correctamente";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        PistaResponseDTO p = pistaService.buscarPorId(id);
        PistaRequestDTO dto = new PistaRequestDTO(p.getNombre(), TipoPista.valueOf(p.getTipo()), p.getUbicacion());
        model.addAttribute("dto", dto);
        model.addAttribute("pistaId", id);
        model.addAttribute("tipos", TipoPista.values());
        model.addAttribute("accion", "Editar");
        return "pistas/formulario";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("dto") PistaRequestDTO dto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pistaId", id);
            model.addAttribute("tipos", TipoPista.values());
            model.addAttribute("accion", "Editar");
            return "pistas/formulario";
        }
        pistaService.actualizar(id, dto);
        return "redirect:/web/pistas?exito=Pista actualizada";
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            pistaService.eliminar(id);
            attrs.addFlashAttribute("exito", "Pista eliminada correctamente");
        } catch (ReservaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/pistas";
    }
}
