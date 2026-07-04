package com.moneyassistant.web;

/** Thrown when the LLM could not produce a usable structured expense. */
public class ExtractionException extends RuntimeException {
    public ExtractionException(String message) {
        super(message);
    }
}
