package com.davi.sistema_de_assinaturas.dto.response;

import java.time.LocalDateTime;

public record ProjectResponseDTO(
        Long id,
        Long customerId,
        String customerName,
        String name,
        LocalDateTime createdAt
) {
}
