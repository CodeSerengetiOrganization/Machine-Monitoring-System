package com.mytech.machinemonitorsystem.handler;

import com.mytech.machinemonitorsystem.controller.MachineMonitorController;
import com.mytech.model.v1.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MachineMonitorController.class);
    //Parameter type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        // Log the exception
        logger.warn("GlobalExceptionHandler caught MethodArgumentTypeMismatchException: {}", ex.getMessage());

        // Build a detailed message
        Object invalidValue = ex.getValue();
        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Invalid value '%s' provided for parameter '%s'. Expected type: %s",
                invalidValue, parameterName, requiredType);

        // Build the ErrorResponse using the generated class
        ErrorResponse errorResponse = new ErrorResponse()
                .error("PARAMETER_TYPE_MISMATCH")
                .message(message)
                .details(List.of()) // can include more details if needed
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    //Resource not found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        logger.warn("GlobalExceptionHandler caught NoHandlerFoundException: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .error("RESOURCE_NOT_FOUND")
                .message("The requested API endpoint does not exist. Please check your URL.")
                .details(List.of())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    //Illegal arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        logger.warn("GlobalExceptionHandler caught IllegalArgumentException: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .error("ILLEGAL_ARGUMENT")
                .message(ex.getMessage())
                .details(List.of())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    // Handle @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse()
                .error("VALIDATION_FAILED")
                .message("Validation failed for request")
                .details(details)
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle empty body or malformed JSON in requests body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String errorMsg;
        if (ex.getCause() != null) {
            errorMsg = ex.getCause().getMessage();
        } else {
            errorMsg = "Malformed or empty JSON request body";
        }

        ErrorResponse errorResponse = new ErrorResponse()
                .error("MALFORMED_JSON")
                .message(errorMsg)
                .details(List.of())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    //Generic internal errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex, HttpServletRequest request) {

        // Log the exception with stack trace
        logger.error("An unexpected internal error occurred: {}", ex.getMessage(), ex);

        // Build the ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse()
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .details(List.of())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
