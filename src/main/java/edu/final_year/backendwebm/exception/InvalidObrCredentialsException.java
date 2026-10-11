package edu.final_year.backendwebm.exception;

public class InvalidObrCredentialsException extends RuntimeException {

    public InvalidObrCredentialsException() {
        super("The provided OBR username/password could not be verified with OBR");
    }
}
