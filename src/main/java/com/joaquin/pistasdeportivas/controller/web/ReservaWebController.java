// controller/web/ReservaWebController.java
package com.joaquin.pistasdeportivas.controller.web;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.exception.*;
import com.joaquin.pistasdeportivas.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/web/reservas")
public class ReservaWebController {

    @Autowired private ReservaService reservaService;
    @Autowired private PistaService   pistaService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public String listar(@AuthenticationPrincipal UserDetails ud, Model model) {
        Usuario u = usuarioService.getEntidadPorEmail(ud.getUsername());
        boolean esAdmin = u.getRol() == Rol.ADMIN;
        model.addAttribute("reservas",
                esAdmin ? reservaService.listarTodas()
                        : reservaService.listarPorUsuario(ud.getUsername()));
        model.addAttribute("esAdmin", esAdmin);
        return "reservas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("dto", new ReservaRequestDTO());
        model.addAttribute("pistas", pistaService.listarTodas());
        return "reservas/formulario";
    }

    @PostMapping("/nueva")
    public String crear(@Valid @ModelAttribute("dto") ReservaRequestDTO dto,
                        BindingResult result, Model model,
                        @AuthenticationPrincipal UserDetails ud,
                        RedirectAttributes attrs) {
        if (result.hasErrors()) {
            model.addAttribute("pistas", pistaService.listarTodas());
            return "reservas/formulario";
        }
        try {
            reservaService.crear(dto, ud.getUsername());
            attrs.addFlashAttribute("exito",
                    "¡Reserva confirmada! Te hemos enviado un email de confirmación.");
            return "redirect:/web/reservas";
        } catch (PistaNoDisponibleException | ReservaException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pistas", pistaService.listarTodas());
            return "reservas/formulario";
        }
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           RedirectAttributes attrs) {
        try {
            ReservaResponseDTO r = reservaService.cancelar(id, ud.getUsername());
            attrs.addFlashAttribute("exito",
                    String.format("Reserva cancelada. Reembolso: %.2f €", r.getReembolso()));
        } catch (ReservaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/reservas";
    }

    @GetMapping("/ingresos")
    @PreAuthorize("hasRole('ADMIN')")
    public String ingresos(Model model,
                           @RequestParam(required = false) Long pistaId,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        model.addAttribute("pistas", pistaService.listarTodas());
        if (pistaId != null && inicio != null && fin != null) {
            model.addAttribute("resultado", reservaService.calcularIngresos(pistaId, inicio, fin));
        }
        return "reservas/ingresos";
    }
}
