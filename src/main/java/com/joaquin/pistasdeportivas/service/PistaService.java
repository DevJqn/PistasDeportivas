package com.joaquin.pistasdeportivas.service;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.exception.*;
import com.joaquin.pistasdeportivas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PistaService {

    @Autowired private PistaRepository pistaRepository;
    @Autowired private ReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<PistaResponseDTO> listarTodas() {
        return pistaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PistaResponseDTO> buscarPorNombre(String nombre) {
        return pistaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PistaResponseDTO> listarDisponibles(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return pistaRepository.findAll().stream()
                .filter(p -> !reservaRepository.existsConflicto(p.getId(), fecha, inicio, fin))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PistaResponseDTO buscarPorId(Long id) {
        return toDTO(getOrThrow(id));
    }

    public Pista getEntidad(Long id) { return getOrThrow(id); }

    public PistaResponseDTO crear(PistaRequestDTO dto) {
        Pista p = Pista.builder()
                .nombre(dto.getNombre()).tipo(dto.getTipo()).ubicacion(dto.getUbicacion())
                .build();
        return toDTO(pistaRepository.save(p));
    }

    public PistaResponseDTO actualizar(Long id, PistaRequestDTO dto) {
        Pista p = getOrThrow(id);
        p.setNombre(dto.getNombre());
        p.setTipo(dto.getTipo());
        p.setUbicacion(dto.getUbicacion());
        return toDTO(pistaRepository.save(p));
    }

    public void eliminar(Long id) {
        getOrThrow(id); // verifica existencia
        if (reservaRepository.existsReservasFuturas(id, LocalDate.now())) {
            throw new ReservaException("No se puede eliminar: la pista tiene reservas futuras");
        }
        pistaRepository.deleteById(id);
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private Pista getOrThrow(Long id) {
        return pistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pista no encontrada: " + id));
    }

    public PistaResponseDTO toDTO(Pista p) {
        return PistaResponseDTO.builder()
                .id(p.getId()).nombre(p.getNombre())
                .tipo(p.getTipo().name()).ubicacion(p.getUbicacion())
                .build();
    }
}

