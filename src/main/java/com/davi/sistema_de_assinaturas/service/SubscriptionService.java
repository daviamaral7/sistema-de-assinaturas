package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.SubscriptionRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.SubscriptionResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.SubscriptionMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import com.davi.sistema_de_assinaturas.repository.PlanRepository;
import com.davi.sistema_de_assinaturas.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionResponseDTO save(SubscriptionRequestDTO dto) {
        Customer customer = customerRepository.findByIdAndStatusNot(dto.customerId(), CustomerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new InvalidBusinessRuleException("Blocked customer cannot create subscription");
        }

        Plan plan = planRepository.findByIdAndActiveTrue(dto.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (subscriptionRepository.existsByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE)) {
            throw new InvalidBusinessRuleException("Customer already has an active subscription");
        }

        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        LocalDate now = LocalDate.now();
        subscription.setStartDate(now);
        subscription.setNextBillingDate(plan.getBillingCycle().addTo(now));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toResponse(savedSubscription);
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionResponseDTO> getByCustomer(Long customerId, Pageable pageable) {
        customerRepository.findByIdAndStatusNot(customerId, CustomerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return subscriptionRepository.findAllByCustomerId(customerId, pageable).map(subscriptionMapper::toResponse);
    }

    public void cancel(Long id) {
        Subscription subscription = subscriptionRepository
                .findByIdAndStatus(id, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setEndDate(LocalDate.now());
    }
}
