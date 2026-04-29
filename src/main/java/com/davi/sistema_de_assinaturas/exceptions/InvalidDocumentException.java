package com.davi.sistema_de_assinaturas.exceptions;

public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException(String message) {
        super(message);
    }
}
