/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.crypto;

import io.github.aigled.dsse.DSSEException;
import io.github.aigled.dsse.DSSESigner;
import lombok.NonNull;

import java.security.*;
import java.util.Objects;

/**
 * Implements the {@link DSSESigner} interface to provide ECDSA-based cryptographic signing for data.
 * This class ensures the integrity and authenticity of the signing process within the DSSE framework
 * using the Elliptic Curve Digital Signature Algorithm (ECDSA).
 * <p>
 * The {@code ECDSASigner} class is initialized with a specific signing algorithm and a private key.
 * It validates that the provided private key is of the EC (Elliptic Curve) type. The signing process
 * is performed using the specified algorithm and the private key.
 */
public class ECDSASigner implements DSSESigner {

    private final String keyId;
    private final String algorithm;
    private final PrivateKey privateKey;

    /**
     * Constructs a new {@code ECDSASigner} instance with the specified keyid, signing algorithm,
     * and private key. The private key must be of type EC (Elliptic Curve).
     *
     * @param keyId
     *         an optional, unauthenticated hint indicating what key and algorithm were used to sign the message; may be {@code null}
     * @param algorithm
     *         the signing algorithm to be used, e.g., "SHA256withECDSA"; must not be null
     * @param privateKey
     *         the private key to be used for signing; must not be null and must
     *         have the algorithm type "EC"
     * @throws IllegalArgumentException
     *         if the private key algorithm is not "EC".
     */
    public ECDSASigner(String keyId, @NonNull String algorithm, @NonNull PrivateKey privateKey) {

        if (!"EC".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be EC");
        }

        this.keyId = keyId;
        this.algorithm = algorithm;
        this.privateKey = privateKey;
    }

    /**
     * Constructs a new {@code ECDSASigner} instance with the specified signing algorithm
     * and private key. The private key must be of type EC (Elliptic Curve).
     *
     * @param algorithm
     *         the signing algorithm to be used, e.g., "SHA256withECDSA"; must not be null
     * @param privateKey
     *         the private key to be used for signing; must not be null and must
     *         have the algorithm type "EC"
     * @throws IllegalArgumentException
     *         if the private key algorithm is not "EC".
     */
    public ECDSASigner(String algorithm, PrivateKey privateKey) {

        this(null, algorithm, privateKey);
    }

    @Override
    public String getKeyId() {

        return this.keyId;
    }

    @Override
    public byte[] sign(byte[] signingInput) {

        Objects.requireNonNull(signingInput, "signingInput must not be null");

        try {
            Signature sig = Signature.getInstance(this.algorithm);
            sig.initSign(this.privateKey);
            sig.update(signingInput);
            return sig.sign();
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new DSSEException(ex.getMessage(), ex);
        }
    }
}
