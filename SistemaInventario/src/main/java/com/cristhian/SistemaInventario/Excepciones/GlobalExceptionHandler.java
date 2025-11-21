package com.cristhian.SistemaInventario.Excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicadoException.class)
    public ResponseEntity<ErrorRespuesta> manejarDuplicado(DuplicadoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarGenerico(Exception ex) {
        ErrorRespuesta error = new ErrorRespuesta("Error interno del servidor");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorRespuesta> manejarValidacion(ValidacionException ex) {
        ErrorRespuesta error = new ErrorRespuesta(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}


