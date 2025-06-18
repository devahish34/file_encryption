package com.fileencryption.service.controller;

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

import com.fileencryption.service.dto.ShareRequestDto;
import com.fileencryption.service.dto.ShareResponseDto;
import com.fileencryption.service.model.SharedFile;
import com.fileencryption.service.service.ShareService;

@RestController
@RequestMapping("/api/files")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @PostMapping("/share")
    public ResponseEntity<ShareResponseDto> shareFile(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute ShareRequestDto shareRequest,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            String email = principal.getAttribute("email");
            ShareResponseDto response = shareService.shareFile(file, shareRequest, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ShareResponseDto(null, "Error: " + e.getMessage()));
        }
    }

    
    @GetMapping("/shared/{fileId}")
    public ResponseEntity<SharedFile> getSharedFileInfo(@PathVariable String fileId) {
        try {
            SharedFile sharedFile = shareService.getSharedFile(fileId);
            
            // Create a clean copy to remove sensitive information
            SharedFile cleanSharedFile = new SharedFile();
            cleanSharedFile.setId(sharedFile.getId());
            cleanSharedFile.setFileName(sharedFile.getFileName());
            cleanSharedFile.setFileType(sharedFile.getFileType());
            cleanSharedFile.setOwner(sharedFile.getOwner());
            cleanSharedFile.setRecipients(sharedFile.getRecipients());
            cleanSharedFile.setMessage(sharedFile.getMessage());
            cleanSharedFile.setCreatedAt(sharedFile.getCreatedAt());
            cleanSharedFile.setExpiresAt(sharedFile.getExpiresAt());
            
            // We don't include recipient phones (which contain sensitive info)
            // And we're not including the file content itself
            
            return ResponseEntity.ok(cleanSharedFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/decrypt-shared/{fileId}")
    public ResponseEntity<Resource> decryptSharedFile(
            @PathVariable String fileId,
            @RequestParam("email") String email,
            @RequestParam("decryptionKey") String decryptionKey) {
        try {
            byte[] decryptedData = shareService.decryptSharedFile(fileId, email, decryptionKey);

            SharedFile sharedFile = shareService.getSharedFile(fileId);
            ByteArrayResource resource = new ByteArrayResource(decryptedData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sharedFile.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}