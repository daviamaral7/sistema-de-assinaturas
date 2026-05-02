package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.ProjectRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.ProjectResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.ProjectMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.Project;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import com.davi.sistema_de_assinaturas.repository.ProjectRepository;
import com.davi.sistema_de_assinaturas.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ProjectMapper mapper;

    public ProjectResponseDTO create(ProjectRequestDTO dto) {
        Customer customer = customerRepository.findByIdAndStatusNot(dto.customerId(), CustomerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Subscription subscription = subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new InvalidBusinessRuleException("Customer has no active subscription"));

        Integer maxProjects = subscription.getPlan().getMaxProjects();

        if (maxProjects != null) {
            long count = projectRepository.countByCustomerId(customer.getId());

            if (count >= maxProjects) {
                throw new InvalidBusinessRuleException("Project limit reached for this plan");
            }
        }

        Project project = new Project();
        project.setCustomer(customer);
        project.setName(dto.name());

        return mapper.toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return mapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getProjectsByCustomerId(Long id, Pageable pageable) {
        return projectRepository.findAllByCustomerId(id, pageable).map(mapper::toResponse);
    }
}
