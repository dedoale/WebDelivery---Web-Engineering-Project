package it.univaq.webdelivery.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


// utility per l'hashing e la verifica delle password.

public final class PasswordUtil {

    private static final int SALT_BYTES = 16;
    private static final int ROUNDS = 10_000;
    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() {
        // solo metodi statici
    }

    // Genera un nuovo hash (con salt casuale) per la password in chiaro passata.
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("La password non puo' essere null");
        }
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        byte[] digest = digest(salt, password);
        return toHex(salt) + ":" + toHex(digest);
    }

    
    // verifica che la password in chiaro corrisponda al valore salvato
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null || !stored.contains(":")) {
            return false;
        }
        String[] parts = stored.split(":", 2);
        try {
            byte[] salt = fromHex(parts[0]);
            byte[] expected = fromHex(parts[1]);
            byte[] actual = digest(salt, password);
            // confronto a tempo costante, per non facilitare timing attack
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            // stored non era nel formato atteso (es. vecchia password in chiaro)
            return false;
        }
    }

    private static byte[] digest(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] result = password.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < ROUNDS; i++) {
                md.reset();
                md.update(salt);
                md.update(result);
                result = md.digest();
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 e' sempre disponibile in una JVM standard
            throw new IllegalStateException("Algoritmo SHA-256 non disponibile", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Stringa esadecimale non valida");
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }
}