package io.github.aigled.dsse.codec;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSEException;
import io.github.aigled.dsse.DSSESerializer;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class Jackson2JsonDSSESerializer implements DSSESerializer {

    private final JsonMapper jsonMapper = JsonMapper.builder()
                                                    .addModule(new DSSEJacksonModule())
                                                    .changeDefaultPropertyInclusion(include -> include.withValueInclusion(JsonInclude.Include.NON_NULL))
                                                    .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                                                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                                                    .build();

    @Override
    public String serialize(DSSEEnvelope envelope) throws DSSEException {

        try {
            return this.jsonMapper.writeValueAsString(envelope);
        } catch (Exception ex) {
            throw new DSSEException("Failed to serialize DSSE envelope '%s'".formatted(envelope), ex);
        }
    }
}
