package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.ProjectRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.ProjectResponseDTO;
import com.davi.sistema_de_assinaturas.service.ProjectService;
import com.davi.sistema_de_assinaturas.util.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody @Valid ProjectRequestDTO dto) {
        ProjectResponseDTO response = projectService.create(dto);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        ProjectResponseDTO response = projectService.getProjectById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Page<ProjectResponseDTO>> getProjectsByCustomerId(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(projectService.getProjectsByCustomerId(id, pageable));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        projectService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProjectResponseDTO> activate(@PathVariable Long id) {
        ProjectResponseDTO response = projectService.activate(id);

        return ResponseEntity.ok(response);
    }
}
