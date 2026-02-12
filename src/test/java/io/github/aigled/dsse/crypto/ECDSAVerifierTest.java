/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.crypto;

import io.github.aigled.dsse.DSSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ECDSAVerifierTest {

    private Signature signer;
    private ECDSAVerifier verifier;

    @BeforeEach
    void setUp() throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String algorithm = "SHA256withECDSA";
        this.signer = Signature.getInstance("SHA256withECDSA");
        this.signer.initSign(keyPair.getPrivate());
        this.verifier = new ECDSAVerifier(algorithm, keyPair.getPublic());
    }

    /**
     * Test case to verify the public key is verified during object creation.
     */
    @Test
    void constructor_ShouldThrowIllegalArgumentException_WhenPrivateKeyIsNotEC() throws NoSuchAlgorithmException {

        // Arrange
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String algorithm = "SHA256withECDSA";

        // Act & Assert
        assertThatThrownBy(() -> new ECDSAVerifier(algorithm, publicKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The public key algorithm must be EC");
    }

    /**
     * Test the verify method with a valid signature.
     */
    @Test
    void verify_shouldReturnTrue_WithValidSignature() throws Exception {

        // Arrange
        byte[] data = "test content".getBytes();
        this.signer.update(data);
        byte[] signature = this.signer.sign();

        // Act
        boolean isValid = this.verifier.verify(data, signature);

        // Assert
        assertThat(isValid).isTrue();
    }

    /**
     * Test the verify method with an invalid signature.
     */
    @Test
    void verify_shouldReturnFalse_WithInvalidSignature() throws Exception {

        // Arrange
        KeyPairGenerator anotherKeyPairGenerator = KeyPairGenerator.getInstance("EC");
        anotherKeyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair anotherKeyPair = anotherKeyPairGenerator.generateKeyPair();
        Signature anotherSigner = Signature.getInstance("SHA256withECDSA");
        anotherSigner.initSign(anotherKeyPair.getPrivate());
        byte[] data = "test content".getBytes();
        anotherSigner.update(data);
        byte[] signature = anotherSigner.sign();

        // Act
        boolean isValid = this.verifier.verify(data, signature);

        // Assert
        assertThat(isValid).isFalse();
    }

    /**
     * Test the verify method with modified content.
     */
    @Test
    void verify_shouldReturnFalse_WithModifiedContent() throws Exception {

        // Arrange
        byte[] data = "test content".getBytes();
        this.signer.update(data);
        byte[] signature = this.signer.sign();

        // Act
        boolean isValid = this.verifier.verify("modified content".getBytes(), signature);

        // Assert
        assertThat(isValid).isFalse();
    }

    /**
     * Test the verify method with an unsupported algorithm.
     */
    @Test
    void verify_shouldThrowDSSEException_WithUnsupportedAlgorithm() throws Exception {

        // Arrange
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        ECDSAVerifier badVerifier = new ECDSAVerifier("UnsupportedAlgorithm", keyPair.getPublic());

        // Act & Assert
        assertThatThrownBy(() -> badVerifier.verify(new byte[]{}, new byte[]{}))
                .isInstanceOf(DSSEException.class)
                .hasMessage("UnsupportedAlgorithm Signature not available");
    }
}
