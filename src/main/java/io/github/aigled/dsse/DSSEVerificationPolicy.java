package io.github.aigled.dsse;

public interface DSSEVerificationPolicy {

    boolean verify(DSSEEnvelope envelope);
}
