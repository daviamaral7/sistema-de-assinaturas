package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByDocument(String document);
}
