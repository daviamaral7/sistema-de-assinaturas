package com.davi.sistema_de_assinaturas.mapper;

import com.davi.sistema_de_assinaturas.dto.response.InvoiceResponseDTO;
import com.davi.sistema_de_assinaturas.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "subscriptionId", source = "subscription.id")
    @Mapping(target = "customerId", source = "subscription.customer.id")
    @Mapping(target = "customerName", source = "subscription.customer.name")
    @Mapping(target = "planId", source = "subscription.plan.id")
    @Mapping(target = "planName", source = "subscription.plan.name")
    @Mapping(target = "status", expression = "java(invoice.getStatus().name())")
    InvoiceResponseDTO toResponse(Invoice invoice);
}
