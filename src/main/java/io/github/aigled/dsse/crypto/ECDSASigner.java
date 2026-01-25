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

    private final String algorithm;
    private final PrivateKey privateKey;

    public ECDSASigner(@NonNull String algorithm, @NonNull PrivateKey privateKey) {

        if (!"EC".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be EC");
        }

        this.algorithm = algorithm;
        this.privateKey = privateKey;
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
