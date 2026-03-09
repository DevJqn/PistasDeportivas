package com.joaquin.pistasdeportivas.dto;

import com.joaquin.pistasdeportivas.entity.TipoPista;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class PistaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private TipoPista tipo;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;
}
