package com.sweetshop.common.util;

import com.sweetshop.exception.UnauthorizedException;
import com.sweetshop.user.domain.Role;
import com.sweetshop.user.domain.User;

public class RoleChecker {
    
    private RoleChecker() {
        // Utility class - prevent instantiation
    }
    
    public static void requireAdmin(User user, String action) {
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException(
                String.format("Only ADMIN users can %s", action)
            );
        }
    }
}



