package com.Spring_auth.dto;

import com.Spring_auth.enitity.Provider;

import java.time.Instant;
import java.util.Set;

public record UserDto(String name, String email, String password, Long id, String imageUrl, boolean enabled, Provider provider, Set<RoleDto> roles, Instant createdAt, Instant updatedAt) {
}
