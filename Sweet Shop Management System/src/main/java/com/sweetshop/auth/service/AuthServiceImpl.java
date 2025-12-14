package com.sweetshop.auth.service;

import com.sweetshop.auth.dto.LoginRequest;
import com.sweetshop.auth.dto.LoginResponse;
import com.sweetshop.auth.dto.RegisterRequest;
import com.sweetshop.auth.dto.RegisterResponse;
import com.sweetshop.exception.AuthenticationException;
import com.sweetshop.security.JwtTokenProvider;
import com.sweetshop.user.domain.Role;
import com.sweetshop.user.domain.User;
import com.sweetshop.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public RegisterResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getRole().name()
        );
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        
        String storedPassword = userOptional
                .map(User::getPassword)
                .orElse("$2a$10$dummyHashToPreventTimingAttack123456789012345678901234567890"); // Constant dummy hash
        
        if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
            throw new AuthenticationException("Invalid username or password");
        }
        
        User user = userOptional.orElseThrow(() -> 
            new AuthenticationException("Invalid username or password"));
        
        String token = jwtTokenProvider.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return response;
    }
}

