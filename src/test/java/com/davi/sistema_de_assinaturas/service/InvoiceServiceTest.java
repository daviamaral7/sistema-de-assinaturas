package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.response.InvoiceResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.InvoiceMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.Invoice;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;
import com.davi.sistema_de_assinaturas.model.enums.InvoiceStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.InvoiceRepository;
import com.davi.sistema_de_assinaturas.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void givenSubscriptionIsNotActive_whenGenerate_thenThrowsResourceNotFoundException() {
        when(subscriptionRepository.findByIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.generate(1L));

        verifyNoInteractions(invoiceRepository, invoiceMapper);
    }

    @Test
    void givenInvoiceAlreadyExistsForDueDate_whenGenerate_thenThrowsInvalidBusinessRuleException() {
        Subscription subscription = subscription(1L, SubscriptionStatus.ACTIVE, LocalDate.now(), BillingCycle.MONTHLY);

        when(subscriptionRepository.findByIdAndStatus(subscription.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndDueDate(subscription.getId(), subscription.getNextBillingDate()))
                .thenReturn(true);

        assertThrows(InvalidBusinessRuleException.class, () -> invoiceService.generate(subscription.getId()));

        verify(invoiceRepository, never()).save(any(Invoice.class));
        verifyNoInteractions(invoiceMapper);
    }

    @Test
    void givenActiveSubscriptionWithoutInvoiceForDueDate_whenGenerate_thenCreatesOpenInvoiceAndAdvancesNextBillingDate() {
        LocalDate dueDate = LocalDate.now();
        Subscription subscription = subscription(1L, SubscriptionStatus.ACTIVE, dueDate, BillingCycle.MONTHLY);
        InvoiceResponseDTO response = invoiceResponse(10L, subscription, dueDate, InvoiceStatus.OPEN);

        when(subscriptionRepository.findByIdAndStatus(subscription.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndDueDate(subscription.getId(), dueDate)).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(response);

        InvoiceResponseDTO result = invoiceService.generate(subscription.getId());

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());
        Invoice savedInvoice = captor.getValue();

        assertSame(response, result);
        assertEquals(subscription, savedInvoice.getSubscription());
        assertEquals(InvoiceStatus.OPEN, savedInvoice.getStatus());
        assertEquals(subscription.getPlan().getPrice(), savedInvoice.getAmount());
        assertEquals(dueDate, savedInvoice.getDueDate());
        assertEquals(BillingCycle.MONTHLY.addTo(dueDate), subscription.getNextBillingDate());
    }

    @Test
    void givenInvoiceIsNotOpen_whenMarkOverdue_thenThrowsInvalidBusinessRuleException() {
        Invoice invoice = invoice(1L, InvoiceStatus.PAID, LocalDate.now().minusDays(1));

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        assertThrows(InvalidBusinessRuleException.class, () -> invoiceService.markOverdue(invoice.getId()));

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    void givenOpenInvoiceDueToday_whenMarkOverdue_thenThrowsInvalidBusinessRuleException() {
        Invoice invoice = invoice(1L, InvoiceStatus.OPEN, LocalDate.now());

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        assertThrows(InvalidBusinessRuleException.class, () -> invoiceService.markOverdue(invoice.getId()));

        assertEquals(InvoiceStatus.OPEN, invoice.getStatus());
    }

    @Test
    void givenOpenInvoicePastDue_whenMarkOverdue_thenMarksInvoiceOverdueAndSubscriptionPastDue() {
        Invoice invoice = invoice(1L, InvoiceStatus.OPEN, LocalDate.now().minusDays(1));
        Subscription subscription = invoice.getSubscription();

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        invoiceService.markOverdue(invoice.getId());

        assertEquals(InvoiceStatus.OVERDUE, invoice.getStatus());
        assertEquals(SubscriptionStatus.PAST_DUE, subscription.getStatus());
    }

    private static Invoice invoice(Long id, InvoiceStatus status, LocalDate dueDate) {
        Subscription subscription = subscription(1L, SubscriptionStatus.ACTIVE, dueDate, BillingCycle.MONTHLY);

        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setSubscription(subscription);
        invoice.setAmount(subscription.getPlan().getPrice());
        invoice.setDueDate(dueDate);
        invoice.setStatus(status);
        return invoice;
    }

    private static Subscription subscription(Long id, SubscriptionStatus status, LocalDate nextBillingDate, BillingCycle billingCycle) {
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setName("Acme");

        Plan plan = new Plan();
        plan.setId(10L);
        plan.setName("Pro");
        plan.setPrice(BigDecimal.valueOf(99.90));
        plan.setBillingCycle(billingCycle);

        Subscription subscription = new Subscription();
        subscription.setId(id);
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStatus(status);
        subscription.setNextBillingDate(nextBillingDate);
        return subscription;
    }

    private static InvoiceResponseDTO invoiceResponse(Long id, Subscription subscription, LocalDate dueDate, InvoiceStatus status) {
        return new InvoiceResponseDTO(
                id,
                subscription.getId(),
                subscription.getCustomer().getId(),
                subscription.getCustomer().getName(),
                subscription.getPlan().getId(),
                subscription.getPlan().getName(),
                subscription.getPlan().getPrice(),
                dueDate,
                null,
                status.name(),
                null,
                null
        );
    }
}
