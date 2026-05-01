package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsBySubscriptionIdAndDueDate(Long id, LocalDate dueDate);

    Page<Invoice> findAllBySubscriptionId(Long subscriptionId, Pageable pageable);

}
