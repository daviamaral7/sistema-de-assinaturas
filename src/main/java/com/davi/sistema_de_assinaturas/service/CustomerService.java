package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.CustomerRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.CustomerResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidDocumentException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceAlreadyExistsException;
import com.davi.sistema_de_assinaturas.mapper.CustomerMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public CustomerResponseDTO save(CustomerRequestDTO dto) {

        if (customerRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }

        String normalizedDocument = dto.document().replaceAll("\\D", "");

        if (normalizedDocument.length() != 11 && normalizedDocument.length() != 14) {
            throw new InvalidDocumentException("Document must be a valid CPF or CNPJ");
        }

        if (customerRepository.existsByDocument(normalizedDocument)) {
            throw new ResourceAlreadyExistsException("Document already in use");
        }

        Customer customer = mapper.toEntity(dto);

        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setDocument(normalizedDocument);

        Customer savedCustomer = customerRepository.save(customer);

        return mapper.toResponse(savedCustomer);
    }


}
