package com.fileencryption.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fileencryption.service.dto.ShareRequestDto;
import com.fileencryption.service.dto.ShareResponseDto;
import com.fileencryption.service.model.SharedFile;
import com.fileencryption.service.model.User;
import com.fileencryption.service.repository.SharedFileRepository;
import com.fileencryption.service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ShareService {

    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private SharedFileRepository sharedFileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ShareResponseDto shareFile(MultipartFile file, ShareRequestDto shareRequest, String senderEmail) throws Exception {
        // Validate recipient count
        if (shareRequest.getRecipients().size() > 5) {
            throw new IllegalArgumentException("Maximum of 5 recipients allowed");
        }
        
        // Get sender info
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        
        // Generate a random password for encryption
        String encryptionPassword = generateRandomPassword();
        
        // Encrypt the file
        byte[] fileData = file.getBytes();
        byte[] encryptedData = encryptionService.encrypt(fileData, encryptionPassword);
        
        // Create shared file record
        SharedFile sharedFile = new SharedFile();
        sharedFile.setFileName(file.getOriginalFilename());
        sharedFile.setFileType(file.getContentType());
        sharedFile.setOwner(sender);
        sharedFile.setFileContent(Base64.getEncoder().encodeToString(encryptedData));
        sharedFile.setCreatedAt(new Date());
        
        // Set expiration date (e.g., 7 days from now)
        Date expiryDate = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        sharedFile.setExpiresAt(expiryDate);
        
        // Set message body
        sharedFile.setMessage(shareRequest.getMessageBody());
        
        // Process recipients
        List<SharedFile.Recipient> recipients = new ArrayList<>();
        for (ShareRequestDto.RecipientInfo recipientInfo : shareRequest.getRecipients()) {
            SharedFile.Recipient recipient = new SharedFile.Recipient(
                    recipientInfo.getEmail(),
                    recipientInfo.getPhoneNumber(),
                    encryptionPassword
            );
            recipients.add(recipient);
        }
        sharedFile.setRecipients(recipients);
        
        // Save to database
        SharedFile savedFile = sharedFileRepository.save(sharedFile);
        
        // Send emails and SMS
        for (SharedFile.Recipient recipient : recipients) {
            // Send email with link to decrypt
            String decryptUrl = "http://localhost:3000/decrypt-shared/" + savedFile.getId();
            emailService.sendShareNotification(
                    recipient.getEmail(),
                    sender.getName(),
                    sharedFile.getFileName(),
                    sharedFile.getMessage(),
                    decryptUrl
            );
            
            // Send SMS with decryption key
            String smsMessage = "Your decryption key for file '" + sharedFile.getFileName() + 
                    "' shared by " + sender.getName() + " is: " + recipient.getDecryptionKey();
            smsService.sendSms(recipient.getPhoneNumber(), smsMessage);
        }
        
        return new ShareResponseDto(savedFile.getId(), "File successfully shared with " + recipients.size() + " recipients");
    }
    
    public SharedFile getSharedFile(String fileId) {
        return sharedFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Shared file not found"));
    }
    
    public byte[] decryptSharedFile(String fileId, String email, String decryptionKey) throws Exception {
        SharedFile sharedFile = sharedFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Shared file not found"));
        
        // Check if the email is a recipient
        SharedFile.Recipient recipient = sharedFile.getRecipients().stream()
                .filter(r -> r.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("You are not authorized to access this file"));
        
        // Check if decryption key matches
        if (!recipient.getDecryptionKey().equals(decryptionKey)) {
            throw new IllegalArgumentException("Invalid decryption key");
        }
        
        // Check if file has expired
        if (sharedFile.getExpiresAt().before(new Date())) {
            throw new IllegalArgumentException("This shared file has expired");
        }
        
        // Decrypt the file
        byte[] encryptedData = Base64.getDecoder().decode(sharedFile.getFileContent());
        byte[] decryptedData = encryptionService.decrypt(encryptedData, decryptionKey);
        
        // Update access information
        recipient.setHasAccessed(true);
        recipient.setAccessedAt(new Date());
        sharedFileRepository.save(sharedFile);
        
        return decryptedData;
    }
    
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}