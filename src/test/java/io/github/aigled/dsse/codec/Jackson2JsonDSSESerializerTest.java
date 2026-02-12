/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.codec;

import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSEException;
import io.github.aigled.dsse.DSSESignature;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Jackson2JsonDSSESerializerTest {

    private final Jackson2JsonDSSESerializer serializer = new Jackson2JsonDSSESerializer();
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    void shouldSerialize_WithValidEnvelope() {

        // Arrange
        String expectedJson = """
                {
                  "payload": "eyJ0ZXN0IjoidmFsdWUifQ==",
                  "payloadType": "application/vnd.in-toto+json",
                  "signatures": [
                    {
                      "sig": "signature1"
                    }
                  ]
                }
                """;
        JsonNode expectedJsonNode = this.jsonMapper.readTree(expectedJson);

        DSSEEnvelope envelope = DSSEEnvelope.ofSignedMessage(
                "eyJ0ZXN0IjoidmFsdWUifQ==",
                "application/vnd.in-toto+json",
                List.of(new DSSESignature(null, "signature1"))
        );

        // Act
        String result = this.serializer.serialize(envelope);
        JsonNode actualJsonNode = this.jsonMapper.readTree(result);

        // Assert
        assertThat(actualJsonNode).isEqualTo(expectedJsonNode);
    }

    @Test
    void shouldThrowException_WhenEnvelopeIsInvalid() {

        // Arrange
        DSSEEnvelope mockEnvelope = mock(DSSEEnvelope.class);
        when(mockEnvelope.getPayload()).thenThrow(JacksonException.class);

        // Act & Assert
        assertThatThrownBy(() -> this.serializer.serialize(mockEnvelope))
                .isInstanceOf(DSSEException.class)
                .hasMessageContaining("Failed to serialize DSSE envelope");
    }
}
