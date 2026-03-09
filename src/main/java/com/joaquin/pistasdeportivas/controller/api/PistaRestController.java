package com.joaquin.pistasdeportivas.controller.api;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.TipoPista;
import com.joaquin.pistasdeportivas.service.PistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/pistas")
@Tag(name = "Pistas")
public class PistaRestController {

    @Autowired private PistaService pistaService;

    @GetMapping
    @Operation(summary = "Listar pistas (opcional: filtrar por nombre)")
    public ResponseEntity<List<PistaResponseDTO>> listar(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(
                nombre != null ? pistaService.buscarPorNombre(nombre) : pistaService.listarTodas());
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Pistas disponibles para una fecha y horario")
    public ResponseEntity<List<PistaResponseDTO>> disponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin) {
        return ResponseEntity.ok(pistaService.listarDisponibles(fecha, horaInicio, horaFin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PistaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pistaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear pista [ADMIN]")
    public ResponseEntity<PistaResponseDTO> crear(@Valid @RequestBody PistaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pistaService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar pista [ADMIN]")
    public ResponseEntity<PistaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody PistaRequestDTO dto) {
        return ResponseEntity.ok(pistaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar pista (solo si sin reservas futuras) [ADMIN]")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
