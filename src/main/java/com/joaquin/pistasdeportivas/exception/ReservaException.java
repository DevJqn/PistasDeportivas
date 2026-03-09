package com.joaquin.pistasdeportivas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservaException extends RuntimeException {
    public ReservaException(String mensaje) { super(mensaje); }
}
