package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    long countByCustomerId(Long id);

    Page<Project> findAllByCustomerId(Long id, Pageable pageable);

    Optional<Project> findByIdAndActiveTrue(Long id);

    Optional<Project> findByIdAndActiveFalse(Long id);

    long countByCustomerIdAndActiveTrue(Long customerId);
}
