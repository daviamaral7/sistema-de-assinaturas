package com.davi.sistema_de_assinaturas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceResponseDTO(
        Long id,
        Long subscriptionId,
        Long customerId,
        String customerName,
        Long planId,
        String planName,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDateTime paidAt,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
