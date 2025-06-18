package com.fileencryption.service.security;

import com.fileencryption.service.model.User;
import com.fileencryption.service.service.UserService;
import com.fileencryption.service.util.JwtTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    public OAuth2LoginSuccessHandler(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        // Process the OAuth2 user (find or create)
        User user = userService.processOAuthUser(oAuth2User);
        
        // Generate JWT token
        String token = jwtTokenUtil.generateToken(user.getEmail());
        
        // Redirect to frontend with token
        String redirectUrl = frontendUrl + "/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}