/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * Represents a DSSE (Dead Simple Signing Envelope) signature.
 * This class encapsulates the signature and associated key identifier,
 * providing functionality for creating and verifying DSSE signatures.
 *
 * @param keyid
 *         optional, unauthenticated hint indicating what key and algorithm were used to sign the message; can be null
 * @param sig
 *         the signature value encoded using standard or URL-safe base64
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/envelope.md">DSSE Envelope</a>
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/protocol.md">DSSE Protocol</a>
 */
public record DSSESignature(String keyid, @NonNull String sig) {

    /**
     * Pre-Authentication Encoding format
     */
    private static final String PAE_FORMAT = "DSSEv1 %d %s %d %s";

    /**
     * Creates a new instance of {@link DSSESignature} using the provided key identifier and raw signature bytes.
     * The signature bytes are encoded in Base64 format before being used to construct the {@code DSSESignature}.
     *
     * @param keyid
     *         an optional, unauthenticated hint indicating what key and algorithm were used to sign
     *         the message; may be {@code null}
     * @param signature
     *         the raw signature bytes to be encoded and used for constructing the {@code DSSESignature}
     * @return a new {@code DSSESignature} object containing the provided key identifier and
     * the Base64-encoded signature
     */
    public static DSSESignature of(String keyid, byte[] signature) {

        String sig = DSSEUtils.base64Encode(signature);
        return new DSSESignature(keyid, sig);
    }

    static String createPreAuthenticationEncoding(String payloadType, byte[] payload) {

        return PAE_FORMAT.formatted(payloadType.length(), payloadType, payload.length, new String(payload, StandardCharsets.UTF_8));
    }

    boolean verify(DSSEVerifier verifier, String pae) {

        return verifier.verify(pae.getBytes(), this.getDecodedSig());
    }

    /**
     * Either standard or URL-safe base64 encodings are allowed.
     *
     * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/protocol.md#protocol">Protocol</a>
     */
    private byte[] getDecodedSig() {

        try {
            return DSSEUtils.base64Decode(this.sig);
        } catch (IllegalArgumentException ex) {
            throw new DSSEException("Unable to Base64 decode signature '%s'".formatted(this.sig), ex);
        }
    }
}
