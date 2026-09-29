package edu.final_year.backendwebm.exception;

public class TvaNotFoundException extends RuntimeException {

    public TvaNotFoundException() {
        super("TVA rate not found");
    }
}
