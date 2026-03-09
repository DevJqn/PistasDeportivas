package com.joaquin.pistasdeportivas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PistaNoDisponibleException extends RuntimeException {
    public PistaNoDisponibleException(String mensaje) { super(mensaje); }
}
