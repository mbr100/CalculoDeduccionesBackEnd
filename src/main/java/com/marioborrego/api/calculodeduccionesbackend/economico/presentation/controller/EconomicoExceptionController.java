package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.ResponseDTO;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.exceptions.EconomicoNoEncontrado;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.exceptions.NewEconomicoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EconomicoExceptionController {

    @ExceptionHandler(NewEconomicoException.class)
    public ResponseEntity<ResponseDTO> handleNewEconomicoException(NewEconomicoException e) {
        String status = "error";
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(status, message));
    }

    @ExceptionHandler(EconomicoNoEncontrado.class)
    public ResponseEntity<ResponseDTO> handleEconomicoNoEncontrado(EconomicoNoEncontrado e) {
        String status = "error";
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(status, message));
    }
}
