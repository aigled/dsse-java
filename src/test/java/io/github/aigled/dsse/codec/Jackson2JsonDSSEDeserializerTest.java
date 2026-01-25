package io.github.aigled.dsse.codec;

import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSEException;
import io.github.aigled.dsse.DSSESignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Jackson2JsonDSSEDeserializerTest {

    private final Jackson2JsonDSSEDeserializer deserializer = new Jackson2JsonDSSEDeserializer();

    @ParameterizedTest
    @ValueSource(strings = {
            "{invalid json}",
            "{\"payload\": null}",
            "{\"payload\":\"eyJ0ZXN0IjoidmFsdWUifQ==\",\"signatures\":[]}",
            "{\"payload\":\"eyJ0ZXN0IjoidmFsdWUifQ==\",\"payloadType\":\"application/vnd.in-toto+json\"}"
    })
    void shouldThrowException_WhenJsonEnvelopeIsInvalid(String json) {

        // Act & Assert
        assertThatThrownBy(() -> this.deserializer.deserialize(json))
                .isInstanceOf(DSSEException.class);
    }

    @Test
    void shouldIgnoreUnrecognizedFields_WhenJsonContainsAdditionalFields() {

        // Arrange
        String json = """
                {
                  "payload": "eyJ0ZXN0IjoidmFsdWUifQ==",
                  "payloadType": "application/vnd.in-toto+json",
                  "field_1": "some value",
                  "signatures": [
                    {
                      "keyid": "key1",
                      "sig": "signature1",
                      "field_2": "some value"
                    }
                  ]
                }
                """;

        // Act
        DSSEEnvelope expectedDSSEEnvelope = DSSEEnvelope.ofSignedMessage(
                "eyJ0ZXN0IjoidmFsdWUifQ==",
                "application/vnd.in-toto+json",
                List.of(new DSSESignature("key1", "signature1"))
        );

        // Act
        DSSEEnvelope result = this.deserializer.deserialize(json);

        // Assert
        assertThat(result).isEqualTo(expectedDSSEEnvelope);
    }

    @Test
    void shouldDeserialize_WhenJsonIsValid() {

        // Arrange
        String json = """
                {
                  "payload": "eyJ0ZXN0IjoidmFsdWUifQ==",
                  "payloadType": "application/vnd.in-toto+json",
                  "signatures": [
                    {
                      "keyid": "key1",
                      "sig": "signature1"
                    }
                  ]
                }
                """;

        DSSEEnvelope expectedDSSEEnvelope = DSSEEnvelope.ofSignedMessage(
                "eyJ0ZXN0IjoidmFsdWUifQ==",
                "application/vnd.in-toto+json",
                List.of(new DSSESignature("key1", "signature1"))
        );

        // Act
        DSSEEnvelope result = this.deserializer.deserialize(json);

        // Assert
        assertThat(result).isEqualTo(expectedDSSEEnvelope);
    }
}
