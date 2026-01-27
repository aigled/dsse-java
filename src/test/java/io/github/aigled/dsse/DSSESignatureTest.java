package io.github.aigled.dsse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DSSESignatureTest {

    @Mock
    private DSSEVerifier verifier;

    @Test
    void createPreAuthenticationEncoding_shouldFormatCorrectly() {

        // Arrange
        String payloadType = "application/json";
        byte[] payload = "test".getBytes();

        // Act
        String pae = DSSESignature.createPreAuthenticationEncoding(payloadType, payload);

        // Assert
        assertThat(pae).isEqualTo("DSSEv1 16 application/json 4 test");
    }

    @Test
    void verify_shouldCallVerifierWithCorrectParameters() {

        // Arrange
        byte[] originalSig = "signature".getBytes();
        String encodedSig = Base64.getEncoder().encodeToString(originalSig);
        DSSESignature signature = new DSSESignature("keyid", encodedSig);
        String pae = "DSSEv1 16 application/json 4 test";

        when(this.verifier.verify(pae.getBytes(), originalSig)).thenReturn(true);

        // Act
        boolean result = signature.verify(this.verifier, pae);

        // Assert
        assertThat(result).isTrue();
        verify(this.verifier).verify(pae.getBytes(), originalSig);
    }

    @Test
    void getDecodedSig_shouldThrowDSSEException_whenInvalidBase64() {

        // Arrange
        String invalidBase64 = "not-valid-base64!!!";
        DSSESignature signature = new DSSESignature("keyid", invalidBase64);
        String pae = "DSSEv1 16 application/json 4 test";

        // Act & Assert
        assertThatThrownBy(() -> signature.verify(this.verifier, pae))
                .isInstanceOf(DSSEException.class)
                .hasMessage("Unable to Base64 decode signature 'not-valid-base64!!!'");
    }

    @Test
    void getDecodedSig_shouldDecodeStandardBase64() {

        // Arrange
        byte[] originalSig = "signature".getBytes();
        String encodedSig = Base64.getEncoder().encodeToString(originalSig);
        DSSESignature signature = new DSSESignature("keyid", encodedSig);
        String pae = "DSSEv1 16 application/json 4 test";

        when(this.verifier.verify(pae.getBytes(), originalSig)).thenReturn(true);

        // Act & Assert
        assertThatNoException().isThrownBy(() -> signature.verify(this.verifier, pae));
    }
}
