package edu.final_year.backendwebm.exception;

public class StaffNotFoundException extends RuntimeException {

    public StaffNotFoundException() {
        super("Staff member not found");
    }
}
