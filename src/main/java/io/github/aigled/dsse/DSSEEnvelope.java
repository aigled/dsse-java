package io.github.aigled.dsse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Envelope is the outermost layer of the attestation, handling authentication and serialization.
 *
 * @see <a href="https://github.com/secure-systems-lab/dsse/blob/master/envelope.md">DSSE Envelope</a>
 */
@Data
@EqualsAndHashCode(of = {"serializedBody", "payloadType", "signatures"})
public class DSSEEnvelope {

    private final byte[] serializedBody;
    private final String payloadType;
    private final List<DSSESignature> signatures = new ArrayList<>();
    private final AtomicReference<State> state = new AtomicReference<>();

    public DSSEEnvelope(byte @NonNull [] serializedBody, @NonNull String payloadType) {

        this.serializedBody = serializedBody;
        this.payloadType = payloadType;
        this.state.set(State.UNSIGNED);
    }

    public static DSSEEnvelope ofSignedMessage(@NonNull String payload, @NonNull String payloadType, @NonNull List<DSSESignature> signatures) {

        byte[] serializedBody = DSSEUtils.base64Decode(payload);
        DSSEEnvelope dsseEnvelope = new DSSEEnvelope(serializedBody, payloadType);
        dsseEnvelope.signatures.addAll(signatures);
        if (!signatures.isEmpty()) {
            dsseEnvelope.state.set(State.SIGNED);
        }

        return dsseEnvelope;
    }

    public String getPayload() {

        return DSSEUtils.base64Encode(this.serializedBody);
    }

    public List<DSSESignature> getSignatures() {

        return List.copyOf(this.signatures);
    }

    public void sign(DSSESigner signer) {

        this.sign(null, signer);
    }

    public synchronized void sign(String keyid, DSSESigner signer) {

        String pae = DSSESignature.createPreAuthenticationEncoding(this.payloadType, this.serializedBody);
        byte[] signedContent = signer.sign(pae.getBytes());
        DSSESignature dsseSignature = DSSESignature.of(keyid, signedContent);
        this.signatures.add(dsseSignature);
        this.state.set(State.SIGNED);
    }

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
