package com.mytech.machinemonitorsystem.handler;

import com.mytech.machinemonitorsystem.controller.MachineMonitorController;
import com.mytech.machinemonitorsystem.entity.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MachineMonitorController.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        logger.warn("GlobalExceptionHandler caught MethodArgumentTypeMismatchException:{}",ex.getMessage());

        Object invalidValue = ex.getValue();
        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() == null ? "unkown" : ex.getRequiredType().getSimpleName();
        String message = String.format("Invalid value %s provided for parameter %s. Expected type: %s", invalidValue, parameterName, requiredType);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,"PARAMETER_TYPE_MISMATCH",message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex){
        logger.warn("GlobalExceptionHandler caught NoHandlerFoundException:{}",ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "The requested API endpoint does not exist. Please check your URL.");
        return  new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex){
        logger.error("An unexpected internal error occurred: {}",ex.getMessage(),ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
