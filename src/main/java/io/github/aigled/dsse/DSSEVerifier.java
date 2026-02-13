/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

/**
 * Verifies DSSE (Dead Simple Signing Envelope) signatures.
 * Implementations of this interface provide signature verification capabilities
 * for DSSE envelopes using specific cryptographic algorithms.
 *
 * @see <a href="https://github.com/secure-systems-lab/dsse">DSSE Specification</a>
 */
public interface DSSEVerifier {

    String getKeyId();

    /**
     * Verifies a signature against signed content.
     *
     * @param data
     *         the content that was signed (typically the Pre-Authentication Encoding)
     * @param signature
     *         the signature to verify
     * @return true if the signature is valid, false otherwise
     * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/protocol.md#signature-definition">Signature Definition</a>
     */
    boolean verify(byte[] data, byte[] signature);
}
