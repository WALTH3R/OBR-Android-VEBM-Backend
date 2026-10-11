package edu.final_year.backendwebm.exception;

public class CommuneNotFoundException extends RuntimeException {

    public CommuneNotFoundException() {
        super("Commune not found");
    }
}
