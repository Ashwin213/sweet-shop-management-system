package com.sweetshop.common.util;

import com.sweetshop.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class RepositoryHelper {
    
    private RepositoryHelper() {
        // Utility class - prevent instantiation
    }
    
    public static <T, ID> T findByIdOrThrow(JpaRepository<T, ID> repository, ID id, String resourceName) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("%s not found with id: %s", resourceName, id)
                ));
    }
}



