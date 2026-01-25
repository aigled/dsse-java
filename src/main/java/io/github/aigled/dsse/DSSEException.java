package io.github.aigled.dsse;

/**
 * A custom exception class for handling DSSE (Dead Simple Signing Envelope) errors.
 * This exception extends {@link RuntimeException} and is used to signal issues encountered
 * within the DSSE operations including encoding, decoding, signing, or verification processes.
 */
public class DSSEException extends RuntimeException {

    public DSSEException(String message, Throwable cause) {

        super(message, cause);
    }
}
