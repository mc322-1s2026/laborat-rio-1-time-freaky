package com.nexus.exception;

/**
 * Exceção customizada lançada quando uma regra de negócio no Nexus é violada.
 */
public class NexusValidationException extends RuntimeException {
    
    public NexusValidationException(String message) {
        super(message);
    }
}