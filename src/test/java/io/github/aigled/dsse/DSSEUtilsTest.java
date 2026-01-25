package io.github.aigled.dsse;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DSSEUtilsTest {

    @Test
    void base64Encode_shouldEncodeByteArrayCorrectly() {

        // Arrange
        byte[] input = "TestString".getBytes(StandardCharsets.UTF_8);

        // Act
        String encoded = DSSEUtils.base64Encode(input);

        // Assert
        String expected = Base64.getEncoder().encodeToString(input);
        assertThat(encoded).isEqualTo(expected);
    }

    @Test
    void base64Encode_shouldHandleEmptyByteArray() {

        // Arrange
        byte[] emptyInput = new byte[0];

        // Act
        String encoded = DSSEUtils.base64Encode(emptyInput);

        // Assert
        assertThat(encoded).isEmpty();
    }

    @Test
    void base64Decode_shouldDecodeValidBase64String() {

        // Act
        byte[] decoded = DSSEUtils.base64Decode("abc+/ro");

        // Assert
        byte[] expected = Base64.getDecoder().decode("abc+/ro");
        assertThat(decoded).isEqualTo(expected);
    }

    @Test
    void base64Decode_shouldDecodeValidUrlSafeBase64String() {

        // Act
        byte[] decoded = DSSEUtils.base64Decode("abc-_ro");

        // Assert
        byte[] expected = Base64.getUrlDecoder().decode("abc-_ro");
        assertThat(decoded).isEqualTo(expected);
    }

    @Test
    void base64Decode_shouldThrowIllegalArgumentExceptionForInvalidBase64() {

        // Arrange
        String invalidBase64 = "##Invalid!!Base64%%";

        // Act & Assert
        assertThatThrownBy(() -> DSSEUtils.base64Decode(invalidBase64))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal base64 character 23");
    }
}
