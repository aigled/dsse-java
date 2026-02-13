/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A verification policy that validates a {@link DSSEEnvelope} using a threshold-based approach.
 * The policy requires a minimum number of valid signatures, provided by trusted verifiers,
 * for the envelope to be considered verified.
 * <p>
 * This class supports optional filtering of verifiers based on the public key ID specified in
 * individual signatures. If the filter is enabled, only the verifier associated with the key ID
 * will be used for verification of that signature. If the filter is disabled, all trusted verifiers
 * will be considered.
 */
@Slf4j
public class ThresholdVerificationPolicy implements DSSEVerificationPolicy {

    private final int threshold;
    private final boolean filterPublicKeyId;
    private final Set<DSSEVerifier> trustedVerifiers;

    /**
     * Constructs a ThresholdVerificationPolicy with the specified configuration.
     *
     * @param threshold
     *         the minimum number of valid signatures required for verification; must be greater than 0
     * @param filterPublicKeyId
     *         a flag indicating whether to filter verifiers based on the public key ID in the signature.
     *         If true, only the matching verifier will be used for each signature. If false, all trusted verifiers
     *         will be considered for each signature.
     * @param trustedVerifiers
     *         a map of public key IDs to their corresponding {@link DSSEVerifier} instances, representing
     *         the trusted verifiers available for signature validation.
     *         The keys in the map represent the public key identifiers, and the values are the associated
     *         {@link DSSEVerifier} implementations.
     * @throws IllegalArgumentException
     *         if the specified threshold is less than or equal to 0
     */
    public ThresholdVerificationPolicy(int threshold, boolean filterPublicKeyId, Set<DSSEVerifier> trustedVerifiers) {

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
            acceptableVerifiers = this.trustedVerifiers.stream()
                                                       .filter(verifier -> verifier.getKeyId() != null
                                                               && verifier.getKeyId().equals(keyId))
                                                       .toList();
            if (acceptableVerifiers.isEmpty()) {
                log.warn("Signature with unknown keyid '{}'", keyId);
            }
        } else {
            acceptableVerifiers = List.copyOf(this.trustedVerifiers);
        }

        return acceptableVerifiers.stream()
                                  .filter(verifier -> signature.verify(verifier, pae))
                                  .findFirst();
    }
}
