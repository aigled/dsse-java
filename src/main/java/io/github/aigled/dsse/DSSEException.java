package io.github.aigled.dsse;

/**
 * A custom exception class for handling DSSE (Dead Simple Signing Envelope) errors.
 * This exception extends {@link RuntimeException} and is used to signal issues encountered
 * within the DSSE operations including encoding, decoding, signing, or verification processes.
 */
public class DSSEException extends RuntimeException {

    /**
     * Constructs a new {@code DSSEException} with the specified detail message and cause.
     * This exception is used to indicate issues encountered during DSSE operations.
     *
     * @param message
     *         the detail message explaining the reason for the exception
     * @param cause
     *         the underlying cause of the exception, which may be {@code null}
     */
    public DSSEException(String message, Throwable cause) {

        super(message, cause);
    }
}
