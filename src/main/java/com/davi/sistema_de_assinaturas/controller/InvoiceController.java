package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.response.InvoiceResponseDTO;
import com.davi.sistema_de_assinaturas.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO response = invoiceService.getInvoiceById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<Page<InvoiceResponseDTO>> getInvoicesBySubscription(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesBySubscription(id, pageable));
    }

    @PatchMapping("/{id}/mark-overdue")
    public ResponseEntity<Void> markOverdue(@PathVariable Long id) {
        invoiceService.markOverdue(id);
        return ResponseEntity.noContent().build();
    }
}
