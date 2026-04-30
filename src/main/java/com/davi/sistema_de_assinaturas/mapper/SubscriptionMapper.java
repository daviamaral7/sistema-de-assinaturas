package com.davi.sistema_de_assinaturas.mapper;

import com.davi.sistema_de_assinaturas.dto.response.SubscriptionResponseDTO;
import com.davi.sistema_de_assinaturas.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "status", expression = "java(subscription.getStatus().name())")
    SubscriptionResponseDTO toResponse(Subscription subscription);

}
