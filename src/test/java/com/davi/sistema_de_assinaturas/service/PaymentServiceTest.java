package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.PaymentRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PaymentResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.mapper.PaymentMapper;
import com.davi.sistema_de_assinaturas.model.Invoice;
import com.davi.sistema_de_assinaturas.model.Payment;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;
import com.davi.sistema_de_assinaturas.model.enums.InvoiceStatus;
import com.davi.sistema_de_assinaturas.model.enums.PaymentMethod;
import com.davi.sistema_de_assinaturas.model.enums.PaymentStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.InvoiceRepository;
import com.davi.sistema_de_assinaturas.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void givenPaidInvoice_whenPay_thenThrowsInvalidBusinessRuleException() {
        Invoice invoice = invoice(1L, InvoiceStatus.PAID, BigDecimal.valueOf(100));
        PaymentRequestDTO request = new PaymentRequestDTO(invoice.getId(), invoice.getAmount(), PaymentMethod.PIX);

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        assertThrows(InvalidBusinessRuleException.class, () -> paymentService.pay(request));

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void givenAmountDoesNotMatchInvoiceAmount_whenPay_thenThrowsInvalidBusinessRuleException() {
        Invoice invoice = invoice(1L, InvoiceStatus.OPEN, BigDecimal.valueOf(100));
        PaymentRequestDTO request = new PaymentRequestDTO(invoice.getId(), BigDecimal.valueOf(90), PaymentMethod.PIX);

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        assertThrows(InvalidBusinessRuleException.class, () -> paymentService.pay(request));

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void givenOpenInvoiceAndNoMoreOverdueInvoices_whenPay_thenPaysInvoiceAndActivatesSubscription() {
        Invoice invoice = invoice(1L, InvoiceStatus.OPEN, BigDecimal.valueOf(100));
        Subscription subscription = invoice.getSubscription();
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        PaymentRequestDTO request = new PaymentRequestDTO(invoice.getId(), invoice.getAmount(), PaymentMethod.CREDIT_CARD);
        PaymentResponseDTO response = paymentResponse(invoice);

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.existsBySubscriptionIdAndStatus(subscription.getId(), InvoiceStatus.OVERDUE))
                .thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(response);

        PaymentResponseDTO result = paymentService.pay(request);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment savedPayment = captor.getValue();

        assertSame(response, result);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertNotNull(invoice.getPaidAt());
        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(invoice, savedPayment.getInvoice());
        assertEquals(request.amount(), savedPayment.getAmount());
        assertEquals(request.paymentMethod(), savedPayment.getPaymentMethod());
        assertEquals(PaymentStatus.APPROVED, savedPayment.getStatus());
        assertEquals(invoice.getPaidAt(), savedPayment.getPaidAt());
    }

    @Test
    void givenOverdueInvoiceAndOtherOverdueInvoicesRemain_whenPay_thenPaysInvoiceAndKeepsSubscriptionPastDue() {
        Invoice invoice = invoice(1L, InvoiceStatus.OVERDUE, BigDecimal.valueOf(100));
        Subscription subscription = invoice.getSubscription();
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        PaymentRequestDTO request = new PaymentRequestDTO(invoice.getId(), invoice.getAmount(), PaymentMethod.PIX);
        PaymentResponseDTO response = paymentResponse(invoice);

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.existsBySubscriptionIdAndStatus(subscription.getId(), InvoiceStatus.OVERDUE))
                .thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(response);

        PaymentResponseDTO result = paymentService.pay(request);

        assertSame(response, result);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertNotNull(invoice.getPaidAt());
        assertEquals(SubscriptionStatus.PAST_DUE, subscription.getStatus());
    }

    private static Invoice invoice(Long id, InvoiceStatus status, BigDecimal amount) {
        Plan plan = new Plan();
        plan.setId(10L);
        plan.setName("Pro");
        plan.setBillingCycle(BillingCycle.MONTHLY);
        plan.setPrice(amount);

        Subscription subscription = new Subscription();
        subscription.setId(20L);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setSubscription(subscription);
        invoice.setAmount(amount);
        invoice.setStatus(status);
        return invoice;
    }

    private static PaymentResponseDTO paymentResponse(Invoice invoice) {
        return new PaymentResponseDTO(
                100L,
                invoice.getId(),
                invoice.getAmount(),
                PaymentMethod.PIX,
                PaymentStatus.APPROVED.name(),
                invoice.getPaidAt()
        );
    }
}
