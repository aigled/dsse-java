/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

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
