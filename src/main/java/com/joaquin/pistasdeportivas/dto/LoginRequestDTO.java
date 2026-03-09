package com.joaquin.pistasdeportivas.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}
