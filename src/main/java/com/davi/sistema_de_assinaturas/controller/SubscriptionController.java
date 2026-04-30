package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.SubscriptionRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.InvoiceResponseDTO;
import com.davi.sistema_de_assinaturas.dto.response.SubscriptionResponseDTO;
import com.davi.sistema_de_assinaturas.service.InvoiceService;
import com.davi.sistema_de_assinaturas.service.SubscriptionService;
import com.davi.sistema_de_assinaturas.util.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> create(@RequestBody @Valid SubscriptionRequestDTO dto) {
        SubscriptionResponseDTO response = subscriptionService.save(dto);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Page<SubscriptionResponseDTO>> getByCustomer(@PathVariable Long customerId, Pageable pageable) {

        return ResponseEntity.ok(subscriptionService.getByCustomer(customerId, pageable));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        subscriptionService.cancel(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invoices/generate")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice (@PathVariable Long id) {
        InvoiceResponseDTO response = invoiceService.generate(id);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }
}
