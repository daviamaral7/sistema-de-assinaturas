package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
