package com.davi.sistema_de_assinaturas.exceptions;

public class InvalidBusinessRuleException extends RuntimeException {
    public InvalidBusinessRuleException(String message) {
        super(message);
    }
}
