// dto/ReservaRequestDTO.java
package com.joaquin.pistasdeportivas.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "Debes seleccionar una pista")
    private Long pistaId;
}

