package com.flowtech.eventhub_spring_boot_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventHubExceptionHandler {

    @ExceptionHandler(TicketNotAvailableException.class)
    public ResponseEntity<String> ticketNotAvailableExceptionHandler(
            TicketNotAvailableException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runTimeExceptionHandler(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
