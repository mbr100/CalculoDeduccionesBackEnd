package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.helper.ResponseErrorDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.exceptions.IDEconomicoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsPersonalController {

    @ExceptionHandler(IDEconomicoException.class)
    public ResponseEntity<ResponseErrorDTO> idEconomicoException(IDEconomicoException e) {
        String status = "error";
        String message = e.getMessage();
        return ResponseEntity.badRequest().body(new ResponseErrorDTO(status, message));
    }
}
