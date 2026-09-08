package com.digitaladvertisingmanagement.auth.application.result;

public record AuthResult(Long userId, String email, String token) {}
