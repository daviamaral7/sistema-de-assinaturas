package com.davi.sistema_de_assinaturas.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubscriptionRequestDTO(
        @NotNull(message = "Customer id is required")
        Long customerId,
        @NotNull(message = "Plan id is required")
        Long planId
) {
}
