package com.davi.sistema_de_assinaturas.dto.response;

import java.time.LocalDateTime;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        String document,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
