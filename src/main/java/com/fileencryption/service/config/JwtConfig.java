
package com.fileencryption.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Bean
    public String jwtSecret() {
        return "your_secure_jwt_secret_key_here";
    }
    
    @Bean
    public Long jwtExpiration() {
        return 86400000L; // 24 hours in milliseconds
    }
}