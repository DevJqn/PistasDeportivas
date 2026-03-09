// service/UsuarioService.java
package com.joaquin.pistasdeportivas.service;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.exception.*;
import com.joaquin.pistasdeportivas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public Usuario registrar(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ReservaException("Ya existe una cuenta con ese email");
        }
        Usuario u = Usuario.builder()
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(Rol.USER)
                .build();
        return usuarioRepository.save(u);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return toDTO(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Usuario getEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO dto) {
    Usuario u = getOrThrow(id);

    if (!u.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
        throw new ReservaException("Ese email ya está en uso");
    }

    u.setNombre(dto.getNombre());
    u.setTelefono(dto.getTelefono());
    u.setEmail(dto.getEmail());

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    // Guardamos también el rol convertido a enum
    if (dto.getRol() != null && !dto.getRol().isBlank()) {
        u.setRol(Rol.valueOf(dto.getRol()));
    }

    return toDTO(usuarioRepository.save(u));
}

    public void eliminar(Long id) {
        usuarioRepository.delete(getOrThrow(id));
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private Usuario getOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    public UsuarioResponseDTO toDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId()).nombre(u.getNombre())
                .telefono(u.getTelefono()).email(u.getEmail())
                .rol(u.getRol().name()).build();
    }
}
