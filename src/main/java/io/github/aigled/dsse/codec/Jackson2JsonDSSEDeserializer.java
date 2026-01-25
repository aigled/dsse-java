package io.github.aigled.dsse.codec;

import io.github.aigled.dsse.DSSEDeserializer;
import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSEException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson-based implementation of {@link DSSEDeserializer} that decodes DSSE envelopes
 * from JSON string representation into {@link DSSEEnvelope} domain objects.
 * <p>
 * This decoder uses Jackson's {@link ObjectMapper} to deserialize JSON-encoded DSSE envelopes.
 *
 * @see DSSEDeserializer
 * @see DSSEEnvelope
 */
public class Jackson2JsonDSSEDeserializer implements DSSEDeserializer {

    private final JsonMapper jsonMapper = JsonMapper.builder()
                                                    .addModule(new DSSEJacksonModule())
                                                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                                    .build();

    @Override
    public DSSEEnvelope deserialize(String content) {

        return this.deserializeJsonEnvelope(content);
    }

    private DSSEEnvelope deserializeJsonEnvelope(String jsonEnvelope) {

        try {
            return this.jsonMapper.readValue(jsonEnvelope, DSSEEnvelope.class);
        } catch (Exception ex) {
            throw new DSSEException("Failed to deserialize JSON envelope '%s'".formatted(jsonEnvelope), ex);
        }
    }
}
