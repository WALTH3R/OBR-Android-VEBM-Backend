package edu.final_year.backendwebm.exception;

public class NifAlreadyRegisteredException extends RuntimeException {

    private NifAlreadyRegisteredException(String message) {
        super(message);
    }

    public static NifAlreadyRegisteredException forOwner(boolean sameUser) {
        return new NifAlreadyRegisteredException(sameUser
                ? "You have already registered a store with this NIF"
                : "This NIF is already registered to another account");
    }
}
