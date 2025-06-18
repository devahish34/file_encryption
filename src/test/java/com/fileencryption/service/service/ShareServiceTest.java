package com.fileencryption.service.service;

import com.fileencryption.service.dto.ShareRequestDto;
import com.fileencryption.service.dto.ShareResponseDto;
import com.fileencryption.service.model.SharedFile;
import com.fileencryption.service.model.User;
import com.fileencryption.service.repository.SharedFileRepository;
import com.fileencryption.service.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ShareServiceTest {

    @Mock
    private SharedFileRepository sharedFileRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EncryptionService encryptionService;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private SmsService smsService;
    
    @InjectMocks
    private ShareService shareService;
    
    private User testUser;
    private MockMultipartFile testFile;
    private ShareRequestDto shareRequest;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        
        // Create test file
        testFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
        
        // Create recipient info list
        List<ShareRequestDto.RecipientInfo> recipients = new ArrayList<>();
        ShareRequestDto.RecipientInfo recipient1 = new ShareRequestDto.RecipientInfo();
        recipient1.setEmail("recipient1@example.com");
        recipient1.setPhoneNumber("+1234567890");
        
        ShareRequestDto.RecipientInfo recipient2 = new ShareRequestDto.RecipientInfo();
        recipient2.setEmail("recipient2@example.com");
        recipient2.setPhoneNumber("+0987654321");
        
        recipients.add(recipient1);
        recipients.add(recipient2);
        
        // Create share request
        shareRequest = new ShareRequestDto();
        shareRequest.setRecipients(recipients);
        shareRequest.setMessageBody("Test message");
        
        // Mock repository methods
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(sharedFileRepository.save(any(SharedFile.class))).thenAnswer(invocation -> {
            SharedFile sf = invocation.getArgument(0);
            if (sf.getId() == null) {
                sf.setId("file-123");
            }
            return sf;
        });
        
        // Mock encryption service
        String mockPassword = "testPassword";
        when(encryptionService.encrypt(any(byte[].class), anyString())).thenReturn("encrypted content".getBytes());
        when(encryptionService.decrypt(any(byte[].class), anyString())).thenReturn("decrypted content".getBytes());
        
        // Mock the random password generation
        doReturn(mockPassword).when(encryptionService).generateRandomPassword();
    }
    
    @Test
    void testShareFile() throws Exception {
        // When
        ShareResponseDto response = shareService.shareFile(testFile, shareRequest, testUser.getEmail());
        
        // Then
        assertNotNull(response);
        assertEquals("file-123", response.getFileId());
        assertTrue(response.getMessage().contains("2 recipients"));
        
        // Verify encryption service was called
        verify(encryptionService).encrypt(any(byte[].class), anyString());
        
        // Verify email service was called for each recipient
        verify(emailService, times(2)).sendShareNotification(
                anyString(), anyString(), anyString(), anyString(), anyString());
        
        // Verify SMS service was called for each recipient
        verify(smsService, times(2)).sendSms(anyString(), anyString());
        
        // Verify file was saved to repository
        verify(sharedFileRepository).save(any(SharedFile.class));
    }
    
    @Test
    void testGetSharedFile() {
        // Given
        String fileId = "file-123";
        SharedFile mockSharedFile = new SharedFile();
        mockSharedFile.setId(fileId);
        mockSharedFile.setFileName("test.txt");
        
        when(sharedFileRepository.findById(fileId)).thenReturn(Optional.of(mockSharedFile));
        
        // When
        SharedFile result = shareService.getSharedFile(fileId);
        
        // Then
        assertNotNull(result);
        assertEquals(fileId, result.getId());
        assertEquals("test.txt", result.getFileName());
        
        // Verify repository method was called
        verify(sharedFileRepository).findById(fileId);
    }
    
    @Test
    void testDecryptSharedFile() throws Exception {
        // Given
        String fileId = "file-123";
        String recipientEmail = "recipient1@example.com";
        String decryptionKey = "testPassword";
        
        SharedFile mockSharedFile = new SharedFile();
        mockSharedFile.setId(fileId);
        mockSharedFile.setFileName("test.txt");
        mockSharedFile.setFileContent(Base64.getEncoder().encodeToString("encrypted content".getBytes()));
        mockSharedFile.setExpiresAt(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        
        List<SharedFile.Recipient> recipients = new ArrayList<>();
        SharedFile.Recipient recipient = new SharedFile.Recipient(recipientEmail, "+1234567890", decryptionKey);
        recipients.add(recipient);
        mockSharedFile.setRecipients(recipients);
        
        when(sharedFileRepository.findById(fileId)).thenReturn(Optional.of(mockSharedFile));
        when(sharedFileRepository.save(any(SharedFile.class))).thenReturn(mockSharedFile);
        
        // When
        byte[] decryptedData = shareService.decryptSharedFile(fileId, recipientEmail, decryptionKey);
        
        // Then
        assertNotNull(decryptedData);
        assertEquals("decrypted content", new String(decryptedData));
        
        // Verify the recipient was marked as having accessed the file
        assertTrue(recipient.isHasAccessed());
        assertNotNull(recipient.getAccessedAt());
        
        // Verify repository methods were called
        verify(sharedFileRepository).findById(fileId);
        verify(sharedFileRepository).save(mockSharedFile);
        verify(encryptionService).decrypt(any(byte[].class), eq(decryptionKey));
    }
}