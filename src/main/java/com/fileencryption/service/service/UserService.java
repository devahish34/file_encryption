package com.fileencryption.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.fileencryption.service.dto.UserDto;
import com.fileencryption.service.model.User;
import com.fileencryption.service.repository.UserRepository;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User processOAuthUser(OAuth2User oAuth2User) {
        // Your existing OAuth2 processing code
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String imageUrl = oAuth2User.getAttribute("picture");
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update existing user info
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setLastLogin(new Date());
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setImageUrl(imageUrl);
            newUser.setProvider("google");
            newUser.setCreatedAt(new Date());
            newUser.setLastLogin(new Date());
            return userRepository.save(newUser);
        }
    }
    
    // Add these new methods for regular user registration
    
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    public UserDto registerUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setProvider("local");
        user.setCreatedAt(new Date());
        user.setLastLogin(new Date());
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    // Helper method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getImageUrl()
        );
    }
    
    public UserDto getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getImageUrl());
        }
        return null;
    }
    
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public UserDto getUserById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getImageUrl());
        }
        return null;
    }
}