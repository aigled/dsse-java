/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.crypto;

import io.github.aigled.dsse.DSSEException;
import io.github.aigled.dsse.DSSEVerifier;
import lombok.NonNull;

import java.security.*;
import java.util.Objects;

/**
 * Implements the {@link DSSEVerifier} interface to provide ECDSA-based cryptographic verification for data.
 * This class ensures the integrity and authenticity of the verification process within the DSSE framework
 * using the Elliptic Curve Digital Signature Algorithm (ECDSA).
 * <p>
 * The {@code ECDSAVerifier} class is initialized with a specific algorithm and a public key.
 * It validates that the provided public key is of the EC (Elliptic Curve) type.
 * The verification process checks if the provided signature matches the content when processed
 * with the specified algorithm and the public key.
 */
public class ECDSAVerifier implements DSSEVerifier {

    private final String keyId;
    private final String algorithm;
    private final PublicKey publicKey;

    /**
     * Constructs a new {@code ECDSAVerifier} instance with the specified keyid, verification algorithm,
     * and public key. The public key must be of type EC (Elliptic Curve).
     *
     * @param keyId
     *         an optional, unauthenticated hint indicating what key and algorithm were used to sign the message; may be {@code null}
     * @param algorithm
     *         the verification algorithm to be used, e.g., "SHA256withECDSA"; must not be null
     * @param publicKey
     *         the public key to be used for verification; must not be null and must
     *         have the algorithm type "EC"
     * @throws IllegalArgumentException
     *         if the public key algorithm is not "EC".
     */
    public ECDSAVerifier(String keyId, @NonNull String algorithm, @NonNull PublicKey publicKey) {

        if (!"EC".equalsIgnoreCase(publicKey.getAlgorithm())) {
            throw new IllegalArgumentException("The public key algorithm must be EC");
        }

        this.keyId = keyId;
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    /**
     * Constructs a new {@code ECDSAVerifier} instance with the specified verification algorithm
     * and public key. The public key must be of type EC (Elliptic Curve).
     *
     * @param algorithm
     *         the verification algorithm to be used, e.g., "SHA256withECDSA"; must not be null
     * @param publicKey
     *         the public key to be used for verification; must not be null and must
     *         have the algorithm type "EC"
     * @throws IllegalArgumentException
     *         if the public key algorithm is not "EC".
     */
    public ECDSAVerifier(String algorithm, PublicKey publicKey) {

        this(null, algorithm, publicKey);
    }

    @Override
    public String getKeyId() {

        return this.keyId;
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {

        Objects.requireNonNull(data, "data must not be null");

        try {
            Signature sig = Signature.getInstance(this.algorithm);
            sig.initVerify(this.publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (SignatureException ex) {
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new DSSEException(ex.getMessage(), ex);
        }
    }
}
