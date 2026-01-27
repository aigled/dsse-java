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

    private final String algorithm;
    private final PublicKey publicKey;

    public ECDSAVerifier(@NonNull String algorithm, @NonNull PublicKey publicKey) {

        if (!"EC".equalsIgnoreCase(publicKey.getAlgorithm())) {
            throw new IllegalArgumentException("The public key algorithm must be EC");
        }

        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {

        Objects.requireNonNull(data, "data must not be null");

        try {
            Signature sig = Signature.getInstance(this.algorithm);
            sig.initVerify(this.publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (SignatureException _) {
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new DSSEException(ex.getMessage(), ex);
        }
    }
}
