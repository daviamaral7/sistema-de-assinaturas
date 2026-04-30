package com.davi.sistema_de_assinaturas.dto.request;

import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PlanRequestDTO(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price,
        @Positive(message = "Max projects must be greater than 0")
        Integer maxProjects,
        @NotNull(message = "Billing cycle is required")
        BillingCycle billingCycle
) {
}
