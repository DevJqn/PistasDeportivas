package com.joaquin.pistasdeportivas.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ErrorResponseDTO {
    private String error;
    private String mensaje;
}
