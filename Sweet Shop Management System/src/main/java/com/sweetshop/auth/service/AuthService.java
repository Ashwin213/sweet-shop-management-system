package com.sweetshop.auth.service;

import com.sweetshop.auth.dto.LoginRequest;
import com.sweetshop.auth.dto.LoginResponse;
import com.sweetshop.auth.dto.RegisterRequest;
import com.sweetshop.auth.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}

