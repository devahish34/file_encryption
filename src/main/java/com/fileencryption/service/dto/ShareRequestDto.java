package com.fileencryption.service.dto;

import java.util.List;

public class ShareRequestDto {
    private List<RecipientInfo> recipients;
    private String messageBody;
    
    public static class RecipientInfo {
        private String email;
        private String phoneNumber;
        
        // Getters and Setters
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
    }

    // Getters and Setters
    public List<RecipientInfo> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(List<RecipientInfo> recipients) {
        this.recipients = recipients;
    }
    
    public String getMessageBody() {
        return messageBody;
    }
    
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}