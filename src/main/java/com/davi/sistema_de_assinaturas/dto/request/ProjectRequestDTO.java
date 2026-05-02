package com.davi.sistema_de_assinaturas.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProjectRequestDTO(
        @NotNull(message = "Customer is required")
        Long customerId,
        @NotNull(message = "Project name is required")
        String name
) {
}
