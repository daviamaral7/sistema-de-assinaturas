package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.response.InvoiceResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.InvoiceMapper;
import com.davi.sistema_de_assinaturas.model.Invoice;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.InvoiceStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.InvoiceRepository;
import com.davi.sistema_de_assinaturas.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceResponseDTO generate (Long id) {
        Subscription subscription = subscriptionRepository.findByIdAndStatus(id, SubscriptionStatus.ACTIVE)
                .orElseThrow(()->new ResourceNotFoundException("Active subscription not found"));

        LocalDate dueDate = subscription.getNextBillingDate();

        if (invoiceRepository.existsBySubscriptionIdAndDueDate(subscription.getId(), dueDate)) {
            throw new InvalidBusinessRuleException("Invoice already generated for this period");
        }

        Invoice invoice = new Invoice();
        invoice.setSubscription(subscription);
        invoice.setStatus(InvoiceStatus.OPEN);
        invoice.setAmount(subscription.getPlan().getPrice());
        invoice.setDueDate(dueDate);

        subscription.setNextBillingDate(subscription.getPlan().getBillingCycle().addTo(dueDate));

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(savedInvoice);
    }
}
