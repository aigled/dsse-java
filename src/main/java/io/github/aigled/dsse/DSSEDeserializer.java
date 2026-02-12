/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

/**
 * A functional interface responsible for deserializing encoded DSSE envelope strings into
 * {@link DSSEEnvelope} objects.
 * <p>
 * Implementations of this interface must provide the {@link #deserialize(String)} method,
 * which takes an encoded DSSE envelope as input and returns a {@link DSSEEnvelope} object
 * that represents the decoded structure. If deserialization fails due to invalid or malformed
 * input, a {@link DSSEException} is thrown to indicate the error.
 *
 * @see DSSEEnvelope
 * @see DSSEException
 */
@FunctionalInterface
public interface DSSEDeserializer {

    /**
     * Deserializes the provided encoded DSSE envelope string into a {@link DSSEEnvelope} object.
     * The content should be a valid serialized representation of a DSSE envelope.
     *
     * @param content
     *         the serialized DSSE envelope string to be deserialized
     * @return a {@link DSSEEnvelope} object representing the deserialized structure of the input string
     * @throws DSSEException
     *         if the provided input string is invalid, malformed, or cannot be deserialized
     */
    DSSEEnvelope deserialize(String content) throws DSSEException;
}
