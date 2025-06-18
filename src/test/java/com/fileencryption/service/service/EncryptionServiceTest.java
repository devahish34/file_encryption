package com.fileencryption.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {
    @InjectMocks
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize the encryptionService if constructor-based initialization is required
        encryptionService = new EncryptionService();
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
        // Create a test file content
        String content = "This is a test file content";
        byte[] fileContent = content.getBytes();

        // Generate a key for encryption (in this case, using a simple password)
        String key = "testPassword123";

        // Encrypt the data
        byte[] encryptedData = encryptionService.encrypt(fileContent, key);
        assertNotNull(encryptedData);
        assertNotEquals(content, new String(encryptedData));

        // Decrypt the data
        byte[] decryptedData = encryptionService.decrypt(encryptedData, key);
        assertNotNull(decryptedData);
        assertEquals(content, new String(decryptedData));
    }

    @Test
    void testWrongPasswordDecryption() {
        try {
            // Create a test content
            String content = "This is a test file content";
            byte[] fileContent = content.getBytes();

            // Use different passwords
            String encryptPassword = "correctPassword123";
            String decryptPassword = "wrongPassword456";

            // Encrypt with correct password
            byte[] encryptedData = encryptionService.encrypt(fileContent, encryptPassword);

            // Attempt to decrypt with wrong password should fail
            assertThrows(Exception.class, () -> {
                encryptionService.decrypt(encryptedData, decryptPassword);
            });
        } catch (Exception e) {
            // Handle the exception that might be thrown during encryption
            fail("Exception occurred during encryption: " + e.getMessage());
        }
    }

    @Test
    void testRandomPasswordGeneration() {
        try {
            // Test the random password generation
            String password1 = encryptionService.generateRandomPassword();
            String password2 = encryptionService.generateRandomPassword();

            // Passwords should be valid
            assertNotNull(password1);
            assertNotNull(password2);

            // Passwords should be different
            assertNotEquals(password1, password2);

            // Password should be of correct length (based on your implementation)
            assertEquals(8, password1.length()); // Assuming UUID substring(0, 8) as in your service
        } catch (Exception e) {
            fail("Exception occurred during password generation: " + e.getMessage());
        }
    }
}