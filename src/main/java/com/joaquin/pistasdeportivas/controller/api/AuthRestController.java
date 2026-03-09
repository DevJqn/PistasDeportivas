package com.joaquin.pistasdeportivas.controller.api;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.Usuario;
import com.joaquin.pistasdeportivas.security.JwtUtil;
import com.joaquin.pistasdeportivas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthRestController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Login → devuelve JWT")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = jwtUtil.generateToken(usuario);
        return ResponseEntity.ok(JwtResponseDTO.builder()
                .token(token).email(usuario.getEmail())
                .nombre(usuario.getNombre()).rol(usuario.getRol().name())
                .build());
    }

    @PostMapping("/registro")
    @Operation(summary = "Registro de nuevo usuario")
    public ResponseEntity<UsuarioResponseDTO> registro(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario u = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.toDTO(u));
    }
}
