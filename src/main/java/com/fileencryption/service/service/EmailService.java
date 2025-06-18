package com.fileencryption.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    public void sendShareNotification(String recipientEmail, String senderName, 
                                     String fileName, String messageBody, String decryptUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(recipientEmail);
            helper.setSubject(senderName + " has shared an encrypted file with you");
            
            String emailContent = "<html><body>" +
                    "<h2>You've received an encrypted file</h2>" +
                    "<p><strong>" + senderName + "</strong> has shared an encrypted file with you: <strong>" + fileName + "</strong></p>" +
                    "<p>Message from sender:</p>" +
                    "<blockquote>" + messageBody + "</blockquote>" +
                    "<p>To decrypt this file, please:</p>" +
                    "<ol>" +
                    "<li>Check your phone for an SMS containing the decryption key</li>" +
                    "<li>Click the button below to access the file</li>" +
                    "<li>Enter the decryption key when prompted</li>" +
                    "</ol>" +
                    "<p><a href=\"" + decryptUrl + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 15px; " +
                    "text-align: center; text-decoration: none; display: inline-block; border-radius: 5px;\">" +
                    "Decrypt File</a></p>" +
                    "<p>This link will expire in 7 days.</p>" +
                    "</body></html>";
            
            helper.setText(emailContent, true); // true indicates HTML content
            
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}