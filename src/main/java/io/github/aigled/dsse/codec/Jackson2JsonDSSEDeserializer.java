/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse.codec;

import io.github.aigled.dsse.DSSEDeserializer;
import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSEException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * A concrete implementation of the {@link DSSEDeserializer} interface, built to deserialize
 * encoded JSON representations of DSSE envelopes into {@link DSSEEnvelope} objects.
 * <p>
 * This class leverages the Jackson library for JSON processing, and uses a custom
 * {@link DSSEJacksonModule} to configure deserialization behavior specific to DSSE envelope
 * structures. Unknown properties found in the input during deserialization are ignored to
 * ensure compatibility with evolving data formats.
 *
 * @see DSSEDeserializer
 * @see DSSEEnvelope
 * @see DSSEException
 * @see DSSEJacksonModule
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
