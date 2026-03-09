// repository/ReservaRepository.java
package com.joaquin.pistasdeportivas.repository;

import com.joaquin.pistasdeportivas.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioOrderByFechaAscHoraInicioAsc(Usuario usuario);

    List<Reserva> findByUsuarioAndEstadoOrderByFechaAscHoraInicioAsc(
            Usuario usuario, EstadoReserva estado);

    List<Reserva> findByEstadoOrderByFechaAscHoraInicioAsc(EstadoReserva estado);

    List<Reserva> findByPistaAndEstadoOrderByFechaAsc(Pista pista, EstadoReserva estado);

    // Comprueba solapamiento de horario en una pista
    @Query("""
        SELECT COUNT(r) > 0 FROM Reserva r
        WHERE r.pista.id = :pistaId
          AND r.fecha = :fecha
          AND r.estado = 'ACTIVA'
          AND NOT (r.horaFin <= :horaInicio OR r.horaInicio >= :horaFin)
        """)
    boolean existsConflicto(@Param("pistaId")    Long pistaId,
                            @Param("fecha")       LocalDate fecha,
                            @Param("horaInicio")  LocalTime horaInicio,
                            @Param("horaFin")     LocalTime horaFin);

    // Ingresos de una pista en un período (solo reservas ACTIVAS)
    @Query("""
        SELECT COALESCE(SUM(r.precio), 0) FROM Reserva r
        WHERE r.pista.id = :pistaId
          AND r.estado = 'ACTIVA'
          AND r.fecha BETWEEN :inicio AND :fin
        """)
    BigDecimal calcularIngresos(@Param("pistaId") Long pistaId,
                                @Param("inicio")  LocalDate inicio,
                                @Param("fin")     LocalDate fin);

    // ¿Tiene la pista reservas futuras activas?
    @Query("""
        SELECT COUNT(r) > 0 FROM Reserva r
        WHERE r.pista.id = :pistaId
          AND r.estado = 'ACTIVA'
          AND r.fecha >= :hoy
        """)
    boolean existsReservasFuturas(@Param("pistaId") Long pistaId,
                                  @Param("hoy")     LocalDate hoy);
}
