/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

/**
 * Defines a verification policy for a DSSE (Dead Simple Signing Envelope) envelope.
 * This policy is responsible for verifying the legitimacy and integrity of a given {@link DSSEEnvelope}
 * according to specific rules or thresholds defined in implementing classes.
 * <p>
 * Implementations of this interface are expected to provide the logic required to evaluate the
 * envelope and confirm its authenticity based on the signatures, payload, and any associated
 * verifiers or configurations.
 */
@FunctionalInterface
public interface DSSEVerificationPolicy {

    /**
     * Verifies the legitimacy and integrity of the provided DSSE envelope.
     * This method evaluates the envelope's signatures, payload, and other associated
     * properties based on a specific verification policy to determine its authenticity.
     *
     * @param envelope
     *         the {@code DSSEEnvelope} instance to be verified; must not be null
     * @return {@code true} if the envelope is successfully verified, {@code false} otherwise
     */
    boolean verify(DSSEEnvelope envelope);
}
