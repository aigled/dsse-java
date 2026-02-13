/*
 * Copyright (c) 2026 Dorian AIGLE
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.aigled.dsse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a DSSE (Dead Simple Signing Envelope) that facilitates signing and verification of serialized content.
 * A DSSE envelope consists of a serialized payload, metadata about the payload type, and a set of associated signatures.
 * <p>
 * The class provides mechanisms to:
 * - Create and manage an envelope around serialized data.
 * - Sign the envelope using cryptographic mechanisms.
 * - Verify the envelope's signatures.
 * <p>
 * The envelope transitions through specific states:
 * - UNSIGNED: The envelope is created but not yet signed.
 * - SIGNED: The envelope has been signed, but its signature has not been verified.
 * - VERIFIED: The envelope is signed, and its signature(s) have been successfully verified.
 * <p>
 * Thread-safety:
 * This class is thread-safe for signing and verification operations since these methods are synchronized.
 * <p>
 * Note:
 * The payload is expected to be in Base64 format for interoperability with DSSE standards.
 *
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/envelope.md">DSSE Envelope</a>
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/protocol.md">DSSE Protocol</a>
 */
@Data
@EqualsAndHashCode(of = {"serializedBody", "payloadType", "signatures"})
public class DSSEEnvelope {

    private final byte[] serializedBody;
    private final String payloadType;
    private final List<DSSESignature> signatures = new ArrayList<>();
    private final AtomicReference<State> state = new AtomicReference<>();

    /**
     * Constructs a new {@code DSSEEnvelope} with the specified serialized body and payload type.
     * Initializes the state of the envelope to {@link State#UNSIGNED}.
     *
     * @param serializedBody
     *         the serialized content of the payload; must not be null
     * @param payloadType
     *         the type of the payload; must not be null
     */
    public DSSEEnvelope(byte @NonNull [] serializedBody, @NonNull String payloadType) {

        this.serializedBody = serializedBody;
        this.payloadType = payloadType;
        this.state.set(State.UNSIGNED);
    }

    /**
     * Constructs a new {@code DSSEEnvelope} instance from the specified signed message payload,
     * payload type, and list of signatures. The payload is Base64-decoded and used to create the
     * serialized body of the envelope. The provided signatures are added to the envelope's list
     * of signatures, and the state of the envelope is updated to {@link State#SIGNED} if at least
     * one signature is provided.
     *
     * @param payload
     *         the Base64-encoded payload representing the serialized content of the envelope; must not be null
     * @param payloadType
     *         the type of the payload; must not be null
     * @param signatures
     *         the list of {@code DSSESignature} objects to include in the envelope; must not be null
     * @return a new {@code DSSEEnvelope} with the specified payload, payload type, and list of signatures
     */
    public static DSSEEnvelope ofSignedMessage(@NonNull String payload, @NonNull String payloadType, @NonNull List<DSSESignature> signatures) {

        byte[] serializedBody = DSSEUtils.base64Decode(payload);
        DSSEEnvelope dsseEnvelope = new DSSEEnvelope(serializedBody, payloadType);
        dsseEnvelope.signatures.addAll(signatures);
        if (!signatures.isEmpty()) {
            dsseEnvelope.state.set(State.SIGNED);
        }

        return dsseEnvelope;
    }

    /**
     * Encodes the serialized body of the DSSE envelope into a Base64-encoded string.
     *
     * @return the Base64-encoded representation of the serialized body.
     */
    public String getPayload() {

        return DSSEUtils.base64Encode(this.serializedBody);
    }

    /**
     * Retrieves an immutable list of DSSE signatures associated with the envelope.
     *
     * @return an immutable {@code List} of {@code DSSESignature} objects representing
     * the signatures associated with the DSSE envelope.
     */
    public List<DSSESignature> getSignatures() {

        return List.copyOf(this.signatures);
    }

    /**
     * Signs the DSSE envelope using the provided {@code DSSESigner} instance, adding a new signature
     * to the envelope and updating its state to {@code SIGNED}.
     * This method uses the Pre-Authentication Encoding (PAE) of the envelope's payload
     * type and serialized body as the input data for signing.
     *
     * @param signer
     *         the {@code DSSESigner} instance responsible for creating the digital signature; must not be null
     */
    public synchronized void sign(DSSESigner signer) {

        String pae = DSSESignature.createPreAuthenticationEncoding(this.payloadType, this.serializedBody);
        byte[] signedContent = signer.sign(pae.getBytes());
        DSSESignature dsseSignature = DSSESignature.of(signer.getKeyId(), signedContent);
        this.signatures.add(dsseSignature);
        this.state.set(State.SIGNED);
    }

    /**
     * Verifies the current DSSE envelope's signatures against the provided verification policy.
     * If verification is successful, the state of the envelope is updated to {@code VERIFIED}.
     *
     * @param policy
     *         the {@code DSSEVerificationPolicy} implementation used to verify the signatures;
     *         must not be null
     * @return {@code true} if the envelope's signatures are successfully verified based on the policy,
     * {@code false} otherwise
     * @throws IllegalStateException
     *         if the envelope is not in a signed or verified state prior to verification
     */
    public synchronized boolean verify(DSSEVerificationPolicy policy) {

        this.ensureSignedOrVerifiedState();

        boolean verified = policy.verify(this);
        if (verified) {
            this.state.set(State.VERIFIED);
        }

        return verified;
    }

    private void ensureSignedOrVerifiedState() {

        if (this.state.get() != State.SIGNED && this.state.get() != State.VERIFIED) {
            throw new IllegalStateException("The envelope must be in a signed or verified state");
        }
    }

    /**
     * Represents the possible states of a {@link DSSEEnvelope}.
     */
    public enum State {

        /**
         * The envelope is not signed yet.
         */
        UNSIGNED,

        /**
         * The envelope is signed, but its signature is not verified.
         */
        SIGNED,

        /**
         * The envelope is signed and its signature was successfully verified.
         */
        VERIFIED
    }
}
