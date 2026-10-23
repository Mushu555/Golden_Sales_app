package com.goldensales.auth.user;

public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserResponse user
) {}