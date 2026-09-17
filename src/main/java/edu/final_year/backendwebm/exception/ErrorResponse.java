package edu.final_year.backendwebm.exception;

import java.util.Map;

public record ErrorResponse(String message, Map<String, String> fieldErrors) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }
}
