/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

/**
 * Functional interface responsible for serializing {@link DSSEEnvelope} objects into their string representations.
 * Implementations of this interface provide mechanisms to convert a DSSE envelope into a serialized format,
 * which may typically be JSON or other structured data formats.
 * <p>
 * The serialization process translates the {@link DSSEEnvelope} instance into an intermediary format that
 * can be stored or transmitted, ensuring a faithful representation of the envelope's data.
 *
 * @see DSSEEnvelope
 * @see DSSEException
 */
@FunctionalInterface
public interface DSSESerializer {

    /**
     * Serializes the given {@link DSSEEnvelope} into its string representation.
     * The serialization process converts the envelope, containing its payload, payload type, and signatures,
     * into a structured format suitable for storage or transmission.
     *
     * @param envelope
     *         the {@link DSSEEnvelope} object to be serialized; must not be null
     * @return a string representation of the serialized {@link DSSEEnvelope}
     * @throws DSSEException
     *         if an error occurs during the serialization process
     */
    String serialize(DSSEEnvelope envelope) throws DSSEException;
}
