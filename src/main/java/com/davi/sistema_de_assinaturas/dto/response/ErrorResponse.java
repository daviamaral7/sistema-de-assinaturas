package com.davi.sistema_de_assinaturas.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
