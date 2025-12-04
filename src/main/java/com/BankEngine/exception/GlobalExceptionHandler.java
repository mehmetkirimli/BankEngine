package com.BankEngine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusiness(BusinessException ex)
  {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST) // 400
        .body(ex.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFound(NotFoundException ex)
  {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND) // 404
        .body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneric(Exception ex)
  {
    ex.printStackTrace(); // log'a da bas
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
        .body("Internal server error");
  }
}
