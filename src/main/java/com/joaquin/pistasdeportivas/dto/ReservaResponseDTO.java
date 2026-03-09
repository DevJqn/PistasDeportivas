package com.joaquin.pistasdeportivas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservaResponseDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precio;
    private String estado;
    private BigDecimal reembolso;
    private String pistaNombre;
    private String tipoPista;
    private String usuarioNombre;
    private String usuarioEmail;
}
