package com.davi.sistema_de_assinaturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerCreateRequestDTO(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email not valid")
        String email,
        @NotBlank(message = "Document is required")
        String document
) {
}
