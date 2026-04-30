package com.davi.sistema_de_assinaturas.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponseDTO(
        Long id,
        Long customerId,
        String customerName,
        Long planId,
        String planName,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate nextBillingDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
