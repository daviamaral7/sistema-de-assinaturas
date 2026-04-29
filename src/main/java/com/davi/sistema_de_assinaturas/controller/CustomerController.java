package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.CustomerCreateRequestDTO;
import com.davi.sistema_de_assinaturas.dto.request.CustomerUpdateRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.CustomerResponseDTO;
import com.davi.sistema_de_assinaturas.service.CustomerService;
import com.davi.sistema_de_assinaturas.util.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerCreateRequestDTO dto) {
        CustomerResponseDTO response = customerService.save(dto);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CustomerUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
