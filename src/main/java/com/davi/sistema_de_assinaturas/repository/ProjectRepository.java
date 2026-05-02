package com.davi.sistema_de_assinaturas.repository;

import com.davi.sistema_de_assinaturas.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    long countByCustomerId(Long id);

    Page<Project> findAllByCustomerId(Long id, Pageable pageable);
}
