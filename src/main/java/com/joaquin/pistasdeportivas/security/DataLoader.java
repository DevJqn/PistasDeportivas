package com.joaquin.pistasdeportivas.security;

import com.joaquin.pistasdeportivas.dto.UsuarioRegistroDTO;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder   passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@pistas.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .telefono("600000000")
                    .email("admin@pistas.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .build();
            usuarioRepository.save(admin);
            System.out.println(">>> Admin creado: admin@pistas.com / admin123");
        }
    }
}
