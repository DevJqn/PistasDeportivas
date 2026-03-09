// dto/UsuarioResponseDTO.java
package com.joaquin.pistasdeportivas.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private String rol;
}
