package com.mytech.machinemonitorsystem.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Objects;

//@Getter // Generates getters for all fields
//@AllArgsConstructor(access = AccessLevel.PRIVATE) // Generates a constructor with all arguments, but keeps it private for internal use
//@NoArgsConstructor(access = AccessLevel.PROTECTED) // Generates a protected no-arg constructor, often needed by frameworks like JPA/Jackson
//@ToString // Generates toString()
public class ErrorResponse {
//    private int statusCode; //(HTTP status code, e.g., 400)
//    private String error;   //(HTTP status reason phrase, e.g., "Bad Request")
//    private String message; //message (a more specific, human-readable message)
//    private LocalDateTime timestamp;

    private final int statusCode;   // HTTP status code (e.g., 400)
    private final String error;     // HTTP status reason phrase (e.g., "Bad Request")
    private final String code;      // Application-specific error code (e.g., "INVALID_INPUT_EMAIL")
    private final String message;   // A more specific, human-readable message
    private final Instant timestamp; // Using LocalDateTime as per your example
    // Public constructor for external use to build the object
    public ErrorResponse(HttpStatus httpStatus, String code, String message) {
        if (httpStatus == null) {
            throw new IllegalArgumentException("HttpStatus cannot be null");
        }
        this.statusCode = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }
    // --- Getter Methods ---

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    // --- toString() Method ---

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", error='" + error + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    // --- equals() and hashCode() Methods ---
    // These are important for proper object comparison, especially when storing
    // objects in collections like Set or using them as keys in Map.
    // Generated based on all final fields to ensure consistency.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return statusCode == that.statusCode &&
                Objects.equals(error, that.error) &&
                Objects.equals(code, that.code) &&
                Objects.equals(message, that.message) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, error, code, message, timestamp);
    }
}
