package com.Spring_auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
