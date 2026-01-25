package io.github.aigled.dsse;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/protocol.md">DSSE Protocol</a>
 */
public record DSSESignature(String keyid,
                            @NonNull String sig) {

    /**
     * Pre-Authentication Encoding format
     */
    private static final String PAE_FORMAT = "DSSEv1 %d %s %d %s";

    public static DSSESignature of(String keyid, byte[] signature) {

        String sig = DSSEUtils.base64Encode(signature);
        return new DSSESignature(keyid, sig);
    }

    static String createPreAuthenticationEncoding(String payloadType, byte[] payload) {

        return PAE_FORMAT.formatted(payloadType.length(),
                                    payloadType,
                                    payload.length,
                                    new String(payload, StandardCharsets.UTF_8));
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
