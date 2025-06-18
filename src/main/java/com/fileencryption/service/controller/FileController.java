package com.fileencryption.service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fileencryption.service.dto.FileResponseDto;
import com.fileencryption.service.model.StoredFile;
import com.fileencryption.service.service.EncryptionService;
import com.fileencryption.service.service.FileStorageService;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/encrypt")
    public ResponseEntity<Resource> encryptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            // Get user email if authenticated
            String userEmail = null;
            if (principal != null) {
                userEmail = principal.getAttribute("email");
            }
            
            byte[] fileData = file.getBytes();
            byte[] encryptedData = encryptionService.encrypt(fileData, password);
            
            String encryptedFileName = file.getOriginalFilename() + ".encrypted";
            ByteArrayResource resource = new ByteArrayResource(encryptedData);
            
            // Store the encrypted file if user is authenticated
            if (userEmail != null) {
                fileStorageService.storeEncryptedFile(encryptedData, encryptedFileName, password, userEmail);
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encryptedFileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/decrypt")
    public ResponseEntity<Resource> decryptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam("originalFileName") String originalFileName,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            // Log decryption request if user is authenticated
            if (principal != null) {
                String userEmail = principal.getAttribute("email");
                // You could log this operation or perform user-specific actions
            }
            
            byte[] encryptedData = file.getBytes();
            byte[] decryptedData = encryptionService.decrypt(encryptedData, password);
            
            ByteArrayResource resource = new ByteArrayResource(decryptedData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // New endpoints for MyFiles functionality
    
    @GetMapping("/my-files")
    public ResponseEntity<List<FileResponseDto>> getMyFiles(@AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String userEmail = principal.getAttribute("email");
            List<FileResponseDto> files = fileStorageService.getFilesByUser(userEmail);
            
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/decrypt/{fileId}")
    public ResponseEntity<Resource> decryptStoredFile(
            @PathVariable Long fileId,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String userEmail = principal.getAttribute("email");
            String password = requestBody.get("password");
            
            StoredFile storedFile = fileStorageService.getFileById(fileId, userEmail);
            if (storedFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            byte[] decryptedData = encryptionService.decrypt(storedFile.getFileContent(), password);
            
            // Remove .encrypted extension if present
            String originalFilename = storedFile.getFileName();
            if (originalFilename.endsWith(".encrypted")) {
                originalFilename = originalFilename.substring(0, originalFilename.length() - 10);
            }
            
            ByteArrayResource resource = new ByteArrayResource(decryptedData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadEncryptedFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String userEmail = principal.getAttribute("email");
            StoredFile storedFile = fileStorageService.getFileById(fileId, userEmail);
            
            if (storedFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            ByteArrayResource resource = new ByteArrayResource(storedFile.getFileContent());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storedFile.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String userEmail = principal.getAttribute("email");
            boolean deleted = fileStorageService.deleteFile(fileId, userEmail);
            
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}