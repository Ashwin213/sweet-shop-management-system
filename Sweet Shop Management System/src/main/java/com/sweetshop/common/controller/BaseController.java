package com.sweetshop.common.controller;

import com.sweetshop.exception.AuthenticationException;
import com.sweetshop.user.domain.User;
import com.sweetshop.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    
    protected final UserRepository userRepository;
    
    protected BaseController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationException("Authentication required. Please provide a valid token.");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found. Token may be invalid."));
    }
}

