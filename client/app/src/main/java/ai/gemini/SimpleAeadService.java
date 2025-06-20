package ai.gemini;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.ByteBuffer;

public class SimpleAeadService {

    private static final String ALGORITHM = "AES";
    private static final String MODE = "GCM";
    private static final String PADDING = "NoPadding";
    private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;

    // GCM recommended nonce size
    private static final int GCM_NONCE_LENGTH = 12;
    // GCM recommended tag size
    private static final int GCM_TAG_LENGTH = 16; // in bytes

    // AES key size in bits (128, 192, or 256) - 256 matches Fernet's base key size
    private static final int AES_KEY_SIZE = 256; // in bits

    private final SecretKey secretKey;

    /**
     * Creates a SimpleAeadService with a given secret key.
     * @param keyBytes The secret key bytes (e.g., 32 bytes for AES-256).
     */
    public SimpleAeadService(byte[] keyBytes) {
         // For AES-256, keyBytes should be 32 bytes
        if (keyBytes.length * 8 != AES_KEY_SIZE) {
            throw new IllegalArgumentException("Key size must be " + (AES_KEY_SIZE / 8) + " bytes for AES-" + AES_KEY_SIZE);
        }
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Generates a new secure AES-256 key.
     * @return A new SecretKey.
     * @throws Exception If key generation fails.
     */
    public static SecretKey generateNewKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(AES_KEY_SIZE, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    /**
     * Encrypts a message using AES/GCM.
     * The output is a byte array containing the Nonce followed by the ciphertext and GCM tag.
     * @param message The message to encrypt.
     * @return Encrypted data (Nonce + Ciphertext + Tag).
     * @throws Exception If encryption fails.
     */
    public byte[] encrypt(byte[] message) throws Exception {
        // Generate a random Nonce (IV)
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        new SecureRandom().nextBytes(nonce); // Cryptographically strong random bytes

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce); // GCM tag size in bits
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        // Encrypt the message
        byte[] ciphertext = cipher.doFinal(message);

        // Combine Nonce and ciphertext+tag
        ByteBuffer byteBuffer = ByteBuffer.allocate(nonce.length + ciphertext.length);
        byteBuffer.put(nonce);
        byteBuffer.put(ciphertext); // This includes the GCM tag at the end
        return byteBuffer.array();
    }

    /**
     * Decrypts data encrypted with AES/GCM.
     * The input data must be the Nonce followed by the ciphertext and GCM tag.
     * Authentication failure (tampering or wrong key) results in an exception.
     * @param encryptedData Encrypted data (Nonce + Ciphertext + Tag).
     * @return The original decrypted message.
     * @throws Exception If decryption or authentication fails.
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        if (encryptedData.length < GCM_NONCE_LENGTH + GCM_TAG_LENGTH) {
            throw new IllegalArgumentException("Encrypted data is too short.");
        }

        // Extract Nonce and ciphertext+tag
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        byteBuffer.get(nonce);
        byte[] ciphertextWithTag = new byte[byteBuffer.remaining()];
        byteBuffer.get(ciphertextWithTag);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        // Decrypt and authenticate
        // If authentication fails, doFinal will throw BadPaddingException (or AEADBadTagException)
        return cipher.doFinal(ciphertextWithTag);
    }

    // --- Helper methods for Base64 encoding/decoding for easier transport/storage ---

    /**
     * Encrypts a message and returns the result as a Base64URL encoded string.
     * @param message The message to encrypt.
     * @return Base64URL encoded string of (Nonce + Ciphertext + Tag).
     * @throws Exception If encryption fails.
     */
    public String encryptToBase64(byte[] message) throws Exception {
        byte[] encryptedBytes = encrypt(message);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a Base64URL encoded string.
     * @param base64Encrypted The Base64URL encoded string of (Nonce + Ciphertext + Tag).
     * @return The original decrypted message.
     * @throws Exception If decoding, decryption, or authentication fails.
     */
    public byte[] decryptFromBase64(String base64Encrypted) throws Exception {
        byte[] encryptedBytes = Base64.getUrlDecoder().decode(base64Encrypted);
        return decrypt(encryptedBytes);
    }


    public static void main(String[] args) throws Exception {
        // --- Key Management ---
        // In a real application, you would NOT hardcode keys like this.
        // You would load the key securely from a file, environment variable,
        // or a key management system.
        // Also, store the *bytes* of the key, not its Base64 representation
        // if you use generateNewKey().

        // Option 1: Generate a new random key (use this for creating a new key)
        // SecretKey newKey = generateNewKey();
        // byte[] keyBytes = newKey.getEncoded();
        // System.out.println("Generated Key (Base64): " + Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes));
        // SimpleAeadService aeadService = new SimpleAeadService(keyBytes);

        // Option 2: Use a known key (replace with your actual 32-byte key)
        // This is just for demonstration.
        byte[] knownKeyBytes = Base64.getUrlDecoder().decode("YOUR_32_BYTE_BASE64_URL_KEY_HERE_"); // Replace with your actual key
        SimpleAeadService aeadService = new SimpleAeadService(knownKeyBytes);


        // --- Usage ---
        String originalMessage = "This is a secret message!";
        System.out.println("Original Message: " + originalMessage);

        // Encrypt
        String encryptedBase64 = aeadService.encryptToBase64(originalMessage.getBytes("UTF-8"));
        System.out.println("Encrypted (Base64): " + encryptedBase64);

        // Decrypt
        byte[] decryptedBytes = aeadService.decryptFromBase64(encryptedBase64);
        String decryptedMessage = new String(decryptedBytes, "UTF-8");
        System.out.println("Decrypted Message: " + decryptedMessage);

        // --- Demonstrate Authentication Failure ---
        System.out.println("\n--- Demonstrating Authentication Failure ---");
        // Tamper with the encrypted data (change a byte)
        String tamperedEncrypted = encryptedBase64.substring(0, encryptedBase64.length() - 5) + "ABCDE"; // Simple tampering

        try {
            System.out.println("Attempting to decrypt tampered data: " + tamperedEncrypted);
            aeadService.decryptFromBase64(tamperedEncrypted);
            System.out.println("Decryption of tampered data SUCCEEDED (This should NOT happen!)");
        } catch (Exception e) {
            System.out.println("Decryption of tampered data FAILED as expected: " + e.getMessage());
            // e.g., javax.crypto.BadPaddingException: mac check in GCM failed
        }

        // Attempt decryption with a different key
         System.out.println("\n--- Demonstrating Wrong Key Failure ---");
        try {
            byte[] wrongKeyBytes = Base64.getUrlDecoder().decode("ANOTHER_32_BYTE_BASE64_URL_KEY_HERE"); // Replace with a different key
             if (new String(wrongKeyBytes).equals(new String(knownKeyBytes))) {
                 System.out.println("Warning: The 'wrong' key is the same as the correct key. Cannot demonstrate wrong key failure.");
             } else {
                 SimpleAeadService wrongKeyService = new SimpleAeadService(wrongKeyBytes);
                 System.out.println("Attempting to decrypt with wrong key...");
                 wrongKeyService.decryptFromBase64(encryptedBase64);
                 System.out.println("Decryption with wrong key SUCCEEDED (This should NOT happen!)");
             }
        } catch (Exception e) {
            System.out.println("Decryption with wrong key FAILED as expected: " + e.getMessage());
             // e.g., javax.crypto.BadPaddingException: mac check in GCM failed
        }
    }
}
