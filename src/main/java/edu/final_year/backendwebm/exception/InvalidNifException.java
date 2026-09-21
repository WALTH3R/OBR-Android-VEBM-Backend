package edu.final_year.backendwebm.exception;

public class InvalidNifException extends RuntimeException {

    public InvalidNifException() {
        super("This NIF is not valid according to OBR");
    }
}
