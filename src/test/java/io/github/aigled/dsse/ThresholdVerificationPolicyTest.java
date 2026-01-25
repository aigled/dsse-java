package io.github.aigled.dsse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThresholdVerificationPolicyTest {

    private Map<String, DSSEVerifier> trustedVerifiers;

    @Mock
    private DSSEVerifier mockVerifierOne;

    @Mock
    private DSSEVerifier mockVerifierTwo;

    @BeforeEach
    void setUp() {

        this.trustedVerifiers = Map.of("keyid-1", this.mockVerifierOne,
                                       "keyid-2", this.mockVerifierTwo);
    }

    @Test
    void constructor_shouldThrowIllegalArgumentException_whenThresholdNonPositive() {

        assertThatThrownBy(() -> new ThresholdVerificationPolicy(0, true, this.trustedVerifiers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("threshold must be > 0");
    }

    @Test
    void verify_shouldReturnTrue_whenThresholdMetWithDistinctVerifiersAndKeyIdFiltering() {

        // Arrange
        byte[] signatureOne = "sig-1".getBytes(StandardCharsets.UTF_8);
        byte[] signatureTwo = "sig-2".getBytes(StandardCharsets.UTF_8);
        DSSESignature dsseSignatureOne = DSSESignature.of("keyid-1", signatureOne);
        DSSESignature dsseSignatureTwo = DSSESignature.of("keyid-2", signatureTwo);
        String payload = Base64.getEncoder().encodeToString("payload".getBytes(StandardCharsets.UTF_8));
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload,
                                                             "application/json",
                                                             List.of(dsseSignatureOne, dsseSignatureTwo));

        when(this.mockVerifierOne.verify(any(byte[].class), eq(signatureOne))).thenReturn(true);
        when(this.mockVerifierTwo.verify(any(byte[].class), eq(signatureTwo))).thenReturn(true);

        ThresholdVerificationPolicy policy = new ThresholdVerificationPolicy(2, true, this.trustedVerifiers);

        // Act
        boolean result = policy.verify(envelope);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void verify_shouldReturnFalse_whenThresholdDoesNotMetWithDistinctVerifiersAndKeyIdFiltering() {

        // Arrange
        byte[] signatureOne = "sig-1".getBytes(StandardCharsets.UTF_8);
        byte[] signatureTwo = "sig-2".getBytes(StandardCharsets.UTF_8);
        DSSESignature dsseSignatureOne = DSSESignature.of("keyid-1", signatureOne);
        DSSESignature dsseSignatureTwo = DSSESignature.of("keyid-2", signatureTwo);
        String payload = Base64.getEncoder().encodeToString("payload".getBytes(StandardCharsets.UTF_8));
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload,
                                                             "application/json",
                                                             List.of(dsseSignatureOne, dsseSignatureTwo));

        when(this.mockVerifierOne.verify(any(byte[].class), eq(signatureOne))).thenReturn(false);
        when(this.mockVerifierTwo.verify(any(byte[].class), eq(signatureTwo))).thenReturn(true);

        ThresholdVerificationPolicy policy = new ThresholdVerificationPolicy(2, true, this.trustedVerifiers);

        // Act
        boolean result = policy.verify(envelope);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void verify_shouldReturnFalse_whenKeyIdUnknownAndFilteringEnabled() {

        // Arrange
        byte[] signatureBytes = "sig".getBytes(StandardCharsets.UTF_8);
        DSSESignature signature = DSSESignature.of("unknown-keyid", signatureBytes);
        String payload = Base64.getEncoder().encodeToString("payload".getBytes(StandardCharsets.UTF_8));
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload, "application/json", List.of(signature));

        ThresholdVerificationPolicy policy = new ThresholdVerificationPolicy(1, true, this.trustedVerifiers);

        // Act
        boolean result = policy.verify(envelope);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(this.mockVerifierOne, this.mockVerifierTwo);
    }

    @Test
    void verify_shouldReturnTrue_whenFilteringDisabledAndVerifierAcceptsSignature() {

        // Arrange
        byte[] signatureBytes = "sig".getBytes(StandardCharsets.UTF_8);
        DSSESignature signature = DSSESignature.of("unknown-keyid", signatureBytes);
        String payload = Base64.getEncoder().encodeToString("payload".getBytes(StandardCharsets.UTF_8));
        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(payload, "application/json", List.of(signature));

        when(this.mockVerifierOne.verify(any(byte[].class), eq(signatureBytes))).thenReturn(true);

        ThresholdVerificationPolicy policy = new ThresholdVerificationPolicy(1, false, this.trustedVerifiers);

        // Act
        boolean result = policy.verify(envelope);

        // Assert
        assertThat(result).isTrue();
    }
}
