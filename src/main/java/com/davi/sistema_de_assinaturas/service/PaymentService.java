package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.PaymentRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PaymentResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.PaymentMapper;
import com.davi.sistema_de_assinaturas.model.Invoice;
import com.davi.sistema_de_assinaturas.model.Payment;
import com.davi.sistema_de_assinaturas.model.enums.InvoiceStatus;
import com.davi.sistema_de_assinaturas.model.enums.PaymentStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.InvoiceRepository;
import com.davi.sistema_de_assinaturas.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;

    public PaymentResponseDTO pay(PaymentRequestDTO dto) {
        Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                .orElseThrow(() -> new InvalidBusinessRuleException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.OPEN && invoice.getStatus() != InvoiceStatus.OVERDUE) {
            throw new ResourceNotFoundException("Invoice cannot be paid");
        }

        if (dto.amount().compareTo(invoice.getAmount()) != 0) {
            throw new InvalidBusinessRuleException("Payment must match invoice amount");
        }

        LocalDateTime paidAt = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(dto.amount());
        payment.setPaymentMethod(dto.paymentMethod());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setPaidAt(paidAt);

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(paidAt);

        boolean hasOverdue = invoiceRepository
                .existsBySubscriptionIdAndStatus(invoice.getSubscription().getId(), InvoiceStatus.OVERDUE);

        if (!hasOverdue) {
            invoice.getSubscription().setStatus(SubscriptionStatus.ACTIVE);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }
}
