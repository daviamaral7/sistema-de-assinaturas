package com.davi.sistema_de_assinaturas.mapper;

import com.davi.sistema_de_assinaturas.dto.request.PlanRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PlanResponseDTO;
import com.davi.sistema_de_assinaturas.model.Plan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    Plan toEntity(PlanRequestDTO request);

    PlanResponseDTO toResponse(Plan plan);
}
