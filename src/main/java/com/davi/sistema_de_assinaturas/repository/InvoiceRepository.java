package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Invoice;
import com.davi.sistema_de_assinaturas.model.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsBySubscriptionIdAndDueDate(Long id, LocalDate dueDate);

    Page<Invoice> findAllBySubscriptionId(Long subscriptionId, Pageable pageable);

    List<Invoice> findAllByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);

    boolean existsBySubscriptionIdAndStatus(Long id, InvoiceStatus invoiceStatus);
}
