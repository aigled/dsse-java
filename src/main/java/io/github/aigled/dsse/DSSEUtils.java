package io.github.aigled.dsse;

import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
final class DSSEUtils {

    static String base64Encode(byte[] src) {

        byte[] base64SerializedBody = Base64.getEncoder().encode(src);
        return new String(base64SerializedBody, StandardCharsets.UTF_8);
    }

    static byte[] base64Decode(String src) {

        try {
            return Base64.getDecoder().decode(src);
        } catch (IllegalArgumentException _) {
            return Base64.getUrlDecoder().decode(src);
        }
    }
}
