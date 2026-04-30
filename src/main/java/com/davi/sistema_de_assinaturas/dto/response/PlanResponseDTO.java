package com.davi.sistema_de_assinaturas.dto.response;

import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlanResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer maxProjects,
        BillingCycle billingCycle,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
