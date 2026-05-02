package com.davi.sistema_de_assinaturas.mapper;

import com.davi.sistema_de_assinaturas.dto.response.PaymentResponseDTO;
import com.davi.sistema_de_assinaturas.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    PaymentResponseDTO toResponse(Payment payment);
}
