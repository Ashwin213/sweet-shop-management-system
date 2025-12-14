package com.sweetshop.security;

import com.sweetshop.user.domain.User;

public interface JwtTokenProvider {
    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    Long getUserIdFromToken(String token);
    String getRoleFromToken(String token);
    boolean isTokenExpired(String token);
}


