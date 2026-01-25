package io.github.aigled.dsse;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ThresholdVerificationPolicy implements DSSEVerificationPolicy {

    private final int threshold;
    private final boolean filterPublicKeyId;
    private final Map<String, DSSEVerifier> trustedVerifiers;

    public ThresholdVerificationPolicy(int threshold, boolean filterPublicKeyId, Map<String, DSSEVerifier> trustedVerifiers) {

        if (threshold <= 0) {
            throw new IllegalArgumentException("threshold must be > 0");
        }

        this.threshold = threshold;
        this.filterPublicKeyId = filterPublicKeyId;
        this.trustedVerifiers = trustedVerifiers;
    }

    @Override
    public boolean verify(DSSEEnvelope envelope) {

        String pae = DSSESignature.createPreAuthenticationEncoding(envelope.getPayloadType(),
                                                                   envelope.getSerializedBody());
        Set<DSSEVerifier> recognizedVerifiers = new HashSet<>();

        boolean isVerified = false;
        for (DSSESignature signature : envelope.getSignatures()) {
            this.verifySignatureAgainstPAE(signature, pae).ifPresent(recognizedVerifiers::add);
            if (recognizedVerifiers.size() >= this.threshold) {
                isVerified = true;
                break;
            }
        }

        return isVerified;
    }

    private Optional<DSSEVerifier> verifySignatureAgainstPAE(DSSESignature signature, String pae) {

        List<DSSEVerifier> acceptableVerifiers;
        if (this.filterPublicKeyId) {
            String keyId = signature.keyid();
            DSSEVerifier verifier = this.trustedVerifiers.get(keyId);
            if (verifier == null) {
                acceptableVerifiers = Collections.emptyList();
                log.warn("Signature with unknown keyid '{}'", keyId);
            } else {
                acceptableVerifiers = List.of(verifier);
            }
        } else {
            acceptableVerifiers = List.copyOf(this.trustedVerifiers.values());
        }

        return acceptableVerifiers.stream()
                                  .filter(verifier -> signature.verify(verifier, pae))
                                  .findFirst();
    }
}
