package com.davi.sistema_de_assinaturas.mapper;

import com.davi.sistema_de_assinaturas.dto.request.CustomerCreateRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.CustomerResponseDTO;
import com.davi.sistema_de_assinaturas.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerCreateRequestDTO request);

    @Mapping(target = "status", expression = "java(customer.getStatus().name())")
    CustomerResponseDTO toResponse(Customer customer);
}
