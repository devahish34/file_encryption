package com.fileencryption.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "shared_files")
public class SharedFile {
    @Id
    private String id;
    
    private String fileName;
    private String fileType;
    private String fileContent; // Base64 encoded encrypted content
    
    @DocumentReference
    private User owner;
    
    private String message;
    
    private Date createdAt;
    private Date expiresAt;
    
    private List<Recipient> recipients;
    
    // Inner class for recipient details
    public static class Recipient {
        private String email;
        private String phoneNumber;
        private String decryptionKey;
        private boolean hasAccessed;
        private Date accessedAt;
        
        // Default constructor
        public Recipient() {
        }
        
        // Constructor with fields
        public Recipient(String email, String phoneNumber, String decryptionKey) {
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.decryptionKey = decryptionKey;
            this.hasAccessed = false;
        }
        
        // Getters and setters
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhoneNumber() {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        
        public String getDecryptionKey() {
            return decryptionKey;
        }
        
        public void setDecryptionKey(String decryptionKey) {
            this.decryptionKey = decryptionKey;
        }
        
        public boolean isHasAccessed() {
            return hasAccessed;
        }
        
        public void setHasAccessed(boolean hasAccessed) {
            this.hasAccessed = hasAccessed;
        }
        
        public Date getAccessedAt() {
            return accessedAt;
        }
        
        public void setAccessedAt(Date accessedAt) {
            this.accessedAt = accessedAt;
        }
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public String getFileContent() {
        return fileContent;
    }
    
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public List<Recipient> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

	
}