package edu.final_year.backendwebm.exception;

public class ProvinceNotFoundException extends RuntimeException {

    public ProvinceNotFoundException() {
        super("Province not found");
    }
}
