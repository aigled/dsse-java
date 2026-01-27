package io.github.aigled.dsse.codec;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.aigled.dsse.DSSEEnvelope;
import io.github.aigled.dsse.DSSESignature;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;

class DSSEJacksonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {

        this.setMixInAnnotation(DSSEEnvelope.class, DSSEEnvelopeMixin.class);
        super.setupModule(context);
    }

    private static class DSSEEnvelopeMixin {

        @JsonCreator
        public static DSSEEnvelope ofSignedMessage(String payload, String payloadType, List<DSSESignature> signatures) {

            throw new UnsupportedOperationException();
        }
    }
}
