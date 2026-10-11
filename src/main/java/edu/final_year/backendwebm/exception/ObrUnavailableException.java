package edu.final_year.backendwebm.exception;

public class ObrUnavailableException extends RuntimeException {

    public ObrUnavailableException(Throwable cause) {
        super("Could not reach OBR right now. Please try again later", cause);
    }
}
