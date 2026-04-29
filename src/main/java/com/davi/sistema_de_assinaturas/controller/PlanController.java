package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.PlanRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PlanResponseDTO;
import com.davi.sistema_de_assinaturas.service.PlanService;
import com.davi.sistema_de_assinaturas.util.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponseDTO> create(@RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO response = planService.save(dto);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PlanResponseDTO>> getAllPlans(Pageable pageable) {
        return ResponseEntity.ok(planService.getAllPlans(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> update(@PathVariable Long id, @RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO response = planService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
