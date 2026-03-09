package com.joaquin.pistasdeportivas.repository;

import com.joaquin.pistasdeportivas.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByRol(Rol rol);
}
