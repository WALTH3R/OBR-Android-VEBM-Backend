package edu.final_year.backendwebm.exception;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException() {
        super("Sale not found");
    }
}
