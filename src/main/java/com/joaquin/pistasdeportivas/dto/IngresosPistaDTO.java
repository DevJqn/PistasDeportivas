package com.joaquin.pistasdeportivas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IngresosPistaDTO {
    private Long pistaId;
    private String pistaNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalIngresos;
}

