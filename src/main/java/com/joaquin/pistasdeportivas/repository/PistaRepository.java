package com.joaquin.pistasdeportivas.repository;

import com.joaquin.pistasdeportivas.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PistaRepository extends JpaRepository<Pista, Long> {
    List<Pista> findByNombreContainingIgnoreCase(String nombre);
    List<Pista> findByTipo(TipoPista tipo);
    List<Pista> findByUbicacionContainingIgnoreCase(String ubicacion);
}
