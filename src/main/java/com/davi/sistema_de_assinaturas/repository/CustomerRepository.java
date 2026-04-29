package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);

    boolean existsByDocument(String document);

    Optional<Customer> findByIdAndStatusNot(Long id, CustomerStatus status);

    Page<Customer> findAllByStatusNot(CustomerStatus status, Pageable pageable);
}
