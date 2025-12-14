package com.sweetshop.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AuthToken {
    @Id
    private Long id;
}




