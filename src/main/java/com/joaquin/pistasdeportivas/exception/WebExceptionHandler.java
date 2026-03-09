// exception/WebExceptionHandler.java  ← HTML para MVC
package com.joaquin.pistasdeportivas.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "com.joaquin.pistasdeportivas.controller.web")
public class WebExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return errorMav("No encontrado", ex.getMessage());
    }

    @ExceptionHandler(PistaNoDisponibleException.class)
    public ModelAndView handleConflicto(PistaNoDisponibleException ex) {
        return errorMav("Pista no disponible", ex.getMessage());
    }

    @ExceptionHandler(ReservaException.class)
    public ModelAndView handleReserva(ReservaException ex) {
        return errorMav("Error en reserva", ex.getMessage());
    }

    private ModelAndView errorMav(String titulo, String mensaje) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("titulo", titulo);
        mav.addObject("mensaje", mensaje);
        return mav;
    }
}

