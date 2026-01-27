package io.github.aigled.dsse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DSSEEnvelopeTest {

    @Mock
    private DSSESigner signer;

    @Mock
    private DSSEVerificationPolicy mockVerificationPolicy;

    @Test
    void sign_shouldAddSignature_WhenEnvelopeAlreadySigned() {

        // Arrange
        DSSEEnvelope envelope = new DSSEEnvelope("content".getBytes(), "application/json");

        when(this.signer.sign(any())).thenReturn("signature1".getBytes(), "signature2".getBytes());

        // Act
        envelope.sign(this.signer);
        envelope.sign("keyid", this.signer);

        // Assert
        assertThat(envelope.getState().get()).isEqualTo(DSSEEnvelope.State.SIGNED);
        assertThat(envelope.getSignatures()).hasSize(2)
                                            .extracting("keyid")
                                            .containsExactlyInAnyOrder(null, "keyid");
    }

    @Test
    void verify_shouldThrowException_WhenEnvelopeNotSigned() {

        // Arrange
        String payload = Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8));
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload, "application/json", List.of());

        // Act & Assert
        assertThatThrownBy(() -> envelope.verify(this.mockVerificationPolicy))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The envelope must be in a signed or verified state");
    }

    @Test
    void verify_shouldReturnTrue_whenPolicyIsVerified() {

        // Arrange
        String payload = Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8));
        DSSESignature signature = DSSESignature.of("keyid", "signature".getBytes());
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload, "application/json", List.of(signature));

        when(this.mockVerificationPolicy.verify(envelope)).thenReturn(true);

        // Act
        boolean result = envelope.verify(this.mockVerificationPolicy);

        // Assert
        assertThat(result).isTrue();
        assertThat(envelope.getState().get()).isEqualTo(DSSEEnvelope.State.VERIFIED);
    }

    @Test
    void verify_shouldReturnFalse_whenPolicyIsNotVerified() {

        // Arrange
        String payload = Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8));
        DSSESignature signature = DSSESignature.of("keyid", "signature".getBytes());
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload, "application/json", List.of(signature));

        when(this.mockVerificationPolicy.verify(envelope)).thenReturn(false);

        // Act
        boolean result = envelope.verify(this.mockVerificationPolicy);

        // Assert
        assertThat(result).isFalse();
        assertThat(envelope.getState().get()).isEqualTo(DSSEEnvelope.State.SIGNED);
    }
}
