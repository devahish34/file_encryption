package com.fileencryption.service.dto;

public class FileResponseDto {
    private String fileName;
    private String status;
    private String message;
    
    // Default constructor
    public FileResponseDto() {
    }
    
    // Constructor with all fields
    public FileResponseDto(String fileName, String status, String message) {
        this.fileName = fileName;
        this.status = status;
        this.message = message;
    }
    
    // Getters and Setters
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Static method for success response
    public static FileResponseDto success(String fileName, String message) {
        return new FileResponseDto(fileName, "success", message);
    }
    
    // Static method for error response
    public static FileResponseDto error(String fileName, String message) {
        return new FileResponseDto(fileName, "error", message);
    }
}