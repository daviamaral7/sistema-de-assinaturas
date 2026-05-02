package com.davi.sistema_de_assinaturas.dto.response;

import com.davi.sistema_de_assinaturas.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,
        Long invoiceId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String status,
        LocalDateTime paidAt
) {
}
