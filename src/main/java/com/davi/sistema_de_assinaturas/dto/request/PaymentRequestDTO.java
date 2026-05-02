package com.davi.sistema_de_assinaturas.dto.request;

import com.davi.sistema_de_assinaturas.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull(message = "Invoice is required")
        Long invoiceId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}
