package edu.final_year.backendwebm.exception;

public class InvalidPaymentException extends RuntimeException {

    private InvalidPaymentException(String message) {
        super(message);
    }

    public static InvalidPaymentException insufficientAmount() {
        return new InvalidPaymentException("Payment amount is less than the total due");
    }

    public static InvalidPaymentException referenceRequired() {
        return new InvalidPaymentException("A phone number or reference is required for this payment method");
    }

    public static InvalidPaymentException exactAmountRequired() {
        return new InvalidPaymentException("Payment amount must exactly match the total due for this payment method");
    }
}
