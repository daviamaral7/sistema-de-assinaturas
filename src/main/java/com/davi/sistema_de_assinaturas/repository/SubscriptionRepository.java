package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByCustomerIdAndStatus(Long customerId, SubscriptionStatus status);

    Page<Subscription> findAllByCustomerId(Long customerId, Pageable pageable);

}
