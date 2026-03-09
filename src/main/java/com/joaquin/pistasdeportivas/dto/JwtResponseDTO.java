package com.joaquin.pistasdeportivas.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JwtResponseDTO {
    private String token;
    private String email;
    private String nombre;
    private String rol;
}

