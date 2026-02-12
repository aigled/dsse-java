/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ECDSASignerTest {

    private ECDSASigner signer;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String algorithm = "SHA256withECDSA";
        this.signer = new ECDSASigner(algorithm, keyPair.getPrivate());
    }

    /**
     * Test case to verify the private key is verified during object creation.
     */
    @Test
    void constructor_ShouldThrowIllegalArgumentException_WhenPrivateKeyIsNotEC() throws NoSuchAlgorithmException {

        // Arrange
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        String algorithm = "SHA256withECDSA";

        // Act & Assert
        assertThatThrownBy(() -> new ECDSASigner(algorithm, privateKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The private key algorithm must be EC");
    }

    /**
     * Test case to ensure that the signature is correctly generated for valid inputs.
     */
    @Test
    void sign_ShouldGenerateValidSignature_ForValidInputAndKeys() {

        // Arrange
        byte[] inputData = "test input".getBytes();

        // Act
        byte[] signature = this.signer.sign(inputData);

        // Assert
        assertThat(signature).isNotNull().isNotEmpty();
    }

    /**
     * Test case to ensure the exception is thrown for null inputs to sign method.
     */
    @Test
    void sign_ShouldThrowException_WhenInputIsNull() {

        // Act & Assert
        assertThatThrownBy(() -> this.signer.sign(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("signingInput must not be null");
    }
}
