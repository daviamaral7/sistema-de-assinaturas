package com.davi.sistema_de_assinaturas.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorValidationResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> errors,
        String path
) {
}
