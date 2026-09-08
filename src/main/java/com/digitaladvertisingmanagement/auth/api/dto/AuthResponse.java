package com.digitaladvertisingmanagement.auth.api.dto;

public record AuthResponse(Long userId, String email, String token) {}
