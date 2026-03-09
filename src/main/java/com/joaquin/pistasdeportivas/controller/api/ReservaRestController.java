package com.joaquin.pistasdeportivas.controller.api;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas")
public class ReservaRestController {

    @Autowired private ReservaService reservaService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Mis reservas (USER) o todas (ADMIN)")
    public ResponseEntity<List<ReservaResponseDTO>> listar(
            @AuthenticationPrincipal UserDetails ud) {
        Usuario u = usuarioService.getEntidadPorEmail(ud.getUsername());
        List<ReservaResponseDTO> lista = u.getRol() == Rol.ADMIN
                ? reservaService.listarTodas()
                : reservaService.listarPorUsuario(ud.getUsername());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @Operation(summary = "Crear reserva")
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody ReservaRequestDTO dto,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.crear(dto, ud.getUsername()));
    }

    @DeleteMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva (aplica política de reembolso)")
    public ResponseEntity<ReservaResponseDTO> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(reservaService.cancelar(id, ud.getUsername()));
    }

    @GetMapping("/ingresos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ingresos de una pista en un período [ADMIN]")
    public ResponseEntity<IngresosPistaDTO> ingresos(
            @RequestParam Long pistaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reservaService.calcularIngresos(pistaId, inicio, fin));
    }
}
