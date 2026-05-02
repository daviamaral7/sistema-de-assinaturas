package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByName(String name);

    Optional<Plan> findByName(String name);

    Page<Plan> findAllByActiveTrue(Pageable pageable);

    Optional<Plan> findByIdAndActiveTrue(Long id);
}
