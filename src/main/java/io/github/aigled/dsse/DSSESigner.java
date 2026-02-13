/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

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
     * Retrieves the identifier of the key used for signing operations.
     * The key ID is typically used to indicate the specific cryptographic key
     * employed in the signing process, facilitating key management and verification.
     *
     * @return a string representing the identifier of the signing key
     */
    String getKeyId();

    /**
     * Signs the provided input data using a specific cryptographic signing mechanism.
     *
     * @param signingInput
     *         the data to be signed, typically in the form of a Pre-Authentication Encoding (PAE)
     * @return the digital signature in byte array format produced for the given input
     */
    byte[] sign(byte[] signingInput);
}
