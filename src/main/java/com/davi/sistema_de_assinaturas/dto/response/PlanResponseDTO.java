package com.davi.sistema_de_assinaturas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlanResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer maxProjects,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
