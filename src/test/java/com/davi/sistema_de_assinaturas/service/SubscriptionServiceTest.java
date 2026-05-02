package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.SubscriptionRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.SubscriptionResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.mapper.SubscriptionMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import com.davi.sistema_de_assinaturas.repository.PlanRepository;
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
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void givenCustomerAlreadyHasActiveSubscription_whenSave_thenThrowsInvalidBusinessRuleException() {
        Customer customer = customer(1L, CustomerStatus.ACTIVE);
        Plan plan = plan(10L, BillingCycle.MONTHLY);
        SubscriptionRequestDTO request = new SubscriptionRequestDTO(customer.getId(), plan.getId());

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));
        when(planRepository.findByIdAndActiveTrue(plan.getId())).thenReturn(Optional.of(plan));
        when(subscriptionRepository.existsByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(true);

        assertThrows(InvalidBusinessRuleException.class, () -> subscriptionService.save(request));

        verify(subscriptionRepository, never()).save(any(Subscription.class));
        verifyNoInteractions(subscriptionMapper);
    }

    @Test
    void givenBlockedCustomer_whenSave_thenThrowsInvalidBusinessRuleException() {
        Customer customer = customer(1L, CustomerStatus.BLOCKED);
        SubscriptionRequestDTO request = new SubscriptionRequestDTO(customer.getId(), 10L);

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));

        assertThrows(InvalidBusinessRuleException.class, () -> subscriptionService.save(request));

        verifyNoInteractions(planRepository, subscriptionMapper);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void givenValidMonthlyPlan_whenSave_thenCalculatesNextBillingDateFromStartDate() {
        Customer customer = customer(1L, CustomerStatus.ACTIVE);
        Plan plan = plan(10L, BillingCycle.MONTHLY);
        SubscriptionRequestDTO request = new SubscriptionRequestDTO(customer.getId(), plan.getId());
        SubscriptionResponseDTO response = new SubscriptionResponseDTO(
                100L, customer.getId(), customer.getName(), plan.getId(), plan.getName(),
                SubscriptionStatus.ACTIVE.name(), LocalDate.now(), null, LocalDate.now().plusMonths(1), null, null
        );

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));
        when(planRepository.findByIdAndActiveTrue(plan.getId())).thenReturn(Optional.of(plan));
        when(subscriptionRepository.existsByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(false);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subscriptionMapper.toResponse(any(Subscription.class))).thenReturn(response);

        SubscriptionResponseDTO result = subscriptionService.save(request);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());
        Subscription savedSubscription = captor.getValue();

        assertSame(response, result);
        assertEquals(customer, savedSubscription.getCustomer());
        assertEquals(plan, savedSubscription.getPlan());
        assertEquals(SubscriptionStatus.ACTIVE, savedSubscription.getStatus());
        assertEquals(plan.getBillingCycle().addTo(savedSubscription.getStartDate()), savedSubscription.getNextBillingDate());
    }

    private static Customer customer(Long id, CustomerStatus status) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Acme");
        customer.setStatus(status);
        return customer;
    }

    private static Plan plan(Long id, BillingCycle billingCycle) {
        Plan plan = new Plan();
        plan.setId(id);
        plan.setName("Pro");
        plan.setPrice(BigDecimal.valueOf(99.90));
        plan.setBillingCycle(billingCycle);
        plan.setActive(true);
        return plan;
    }
}
