package com.davi.sistema_de_assinaturas.controller;

import com.davi.sistema_de_assinaturas.dto.request.PaymentRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.PaymentResponseDTO;
import com.davi.sistema_de_assinaturas.service.PaymentService;
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
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> pay(@RequestBody @Valid PaymentRequestDTO dto) {
        PaymentResponseDTO response = paymentService.pay(dto);

        return ResponseEntity.created(ControllerUtils.createHeaderLocation(response.id())).body(response);
    }
}
