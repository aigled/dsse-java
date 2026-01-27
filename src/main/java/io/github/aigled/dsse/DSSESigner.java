package io.github.aigled.dsse;

/**
 * Represents the behavior required for signing data within the DSSE (Dead Simple Signing Envelope) framework.
 * Implementations of this interface provide mechanisms to sign content using specific cryptographic algorithms.
 * <p>
 * The signing process ensures the integrity and authenticity of the DSSE envelope by creating a digital
 * signature over the Pre-Authentication Encoding (PAE) of the payload.
 *
 * @see DSSEEnvelope
 * @see DSSESignature
 */
public interface DSSESigner {

    /**
     * Signs the provided input data using a specific cryptographic signing mechanism.
     *
     * @param signingInput
     *         the data to be signed, typically in the form of a Pre-Authentication Encoding (PAE)
     * @return the digital signature in byte array format produced for the given input
     */
    byte[] sign(byte[] signingInput);
}
