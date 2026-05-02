package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.ProjectRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.ProjectResponseDTO;
import com.davi.sistema_de_assinaturas.service.ProjectService;
import com.davi.sistema_de_assinaturas.util.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
