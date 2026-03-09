// dto/PistaResponseDTO.java
package com.joaquin.pistasdeportivas.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PistaResponseDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String ubicacion;
}

