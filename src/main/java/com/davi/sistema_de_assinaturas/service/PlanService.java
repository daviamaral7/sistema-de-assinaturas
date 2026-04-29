package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.PlanRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PlanResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.ResourceAlreadyExistsException;
import com.davi.sistema_de_assinaturas.exceptions.ResourceNotFoundException;
import com.davi.sistema_de_assinaturas.mapper.PlanMapper;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper mapper;

    private Plan findPlanByIdOrThrow(Long id) {
        return planRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
    }

    public PlanResponseDTO save(PlanRequestDTO dto) {
        String normalizedName = dto.name().trim().toUpperCase();
        if (planRepository.existsByName(normalizedName)) {
            throw new ResourceAlreadyExistsException("Plan already exists");
        }

        Plan plan = mapper.toEntity(dto);
        plan.setName(normalizedName);

        Plan savedPlan = planRepository.save(plan);

        return mapper.toResponse(savedPlan);
    }

    @Transactional(readOnly = true)
    public Page<PlanResponseDTO> getAllPlans(Pageable pageable) {
        return planRepository.findAllByActiveTrue(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PlanResponseDTO getPlanById(Long id) {
        Plan plan = findPlanByIdOrThrow(id);

        return mapper.toResponse(plan);
    }

    public PlanResponseDTO update(Long id, PlanRequestDTO dto) {
        Plan plan = findPlanByIdOrThrow(id);
        String normalizedName = dto.name().trim().toUpperCase();
        Optional<Plan> planByName = planRepository.findByName(normalizedName);

        if (planByName.isPresent() && !planByName.get().getId().equals(plan.getId())) {
            throw new ResourceAlreadyExistsException("Name already in use");
        }

        plan.setName(normalizedName);
        plan.setPrice(dto.price());
        plan.setMaxProjects(dto.maxProjects());

        return mapper.toResponse(plan);
    }

    public void delete(Long id) {
        Plan plan = findPlanByIdOrThrow(id);

        plan.setActive(false);
    }
}
