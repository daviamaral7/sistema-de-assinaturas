package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
