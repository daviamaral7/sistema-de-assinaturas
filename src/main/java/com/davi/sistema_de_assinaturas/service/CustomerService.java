package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.CustomerCreateRequestDTO;
import com.davi.sistema_de_assinaturas.dto.request.CustomerUpdateRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.CustomerResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidDocumentException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceAlreadyExistsException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.CustomerMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    private Customer findCustomerByIdOrThrow(Long id) {
        return customerRepository.findByIdAndStatusNot(id, CustomerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public CustomerResponseDTO save(CustomerCreateRequestDTO dto) {

        String normalizedEmail = dto.email().trim().toLowerCase();

        if (customerRepository.existsByEmail(normalizedEmail)) {
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
        customer.setEmail(normalizedEmail);
        customer.setDocument(normalizedDocument);

        Customer savedCustomer = customerRepository.save(customer);

        return mapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAllByStatusNot(CustomerStatus.DELETED, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = findCustomerByIdOrThrow(id);

        return mapper.toResponse(customer);
    }

    public CustomerResponseDTO update(Long id, CustomerUpdateRequestDTO dto) {
        Customer customer = findCustomerByIdOrThrow(id);
        String normalizedEmail = dto.email().trim().toLowerCase();
        Optional<Customer> existingCustomerByEmail = customerRepository.findByEmail(normalizedEmail);

        if (existingCustomerByEmail.isPresent() && !existingCustomerByEmail.get().getId().equals(customer.getId())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }

        customer.setName(dto.name());
        customer.setEmail(normalizedEmail);

        return mapper.toResponse(customer);
    }

    public void delete(Long id) {
        Customer customer = findCustomerByIdOrThrow(id);

        customer.setStatus(CustomerStatus.DELETED);
    }

}
