package com.printxpress.app.utils;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {
    private static final int ITERATIONS = 12000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {}

    public static String hash(String password) {
        try {
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            byte[] value = derive(password, salt, ITERATIONS);
            return "pbkdf2$" + ITERATIONS + "$"
                + Base64.encodeToString(salt, Base64.NO_WRAP) + "$"
                + Base64.encodeToString(value, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("Password protection unavailable", e);
        }
    }

    public static boolean verify(String password, String stored) {
        if (stored == null) {
            return false;
        }
        if (!stored.startsWith("pbkdf2$")) {
            return constantTime(
                password.getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8)
            );
        }
        try {
            String[] parts = stored.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.decode(parts[2], Base64.NO_WRAP);
            byte[] expected = Base64.decode(parts[3], Base64.NO_WRAP);
            return constantTime(derive(password, salt, iterations), expected);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean needsUpgrade(String stored) {
        return stored == null || !stored.startsWith("pbkdf2$");
    }

    private static byte[] derive(String password, byte[] salt, int iterations) throws Exception {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH))
            .getEncoded();
    }

    private static boolean constantTime(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
