package com.fileencryption.service.dto;

public class UserDto {
    private String id;
    private String email;
    private String name;
    private String imageUrl;
    private String password;
    
    // Default constructor
    public UserDto() {
    }
    
    // Constructor with all fields
    public UserDto(String id, String email, String name, String imageUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}