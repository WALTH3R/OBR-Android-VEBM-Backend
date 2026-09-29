package edu.final_year.backendwebm.exception;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException() {
        super("Store not found");
    }
}
