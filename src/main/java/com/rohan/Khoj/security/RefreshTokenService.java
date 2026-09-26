package com.rohan.Khoj.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final long refreshTokenExpirationMs;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            @Value("${application.security.jwt.refresh-token.expiration:604800000}") long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(String username, UUID userId) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(userId)
                .username(username)
                .token(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""))
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenRefreshResponseDTO refreshAccessToken(String requestRefreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found."));

        if (tokenEntity.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked.");
        }

        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            tokenEntity.setRevoked(true);
            refreshTokenRepository.save(tokenEntity);
            throw new IllegalArgumentException("Refresh token has expired. Please sign in again.");
        }

        // Token Rotation: revoke current and issue a new one
        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        RefreshTokenEntity newRefreshToken = createRefreshToken(tokenEntity.getUsername(), tokenEntity.getUserId());

        UserDetails userDetails = userDetailsService.loadUserByUsername(tokenEntity.getUsername());
        String newAccessToken = jwtService.generateToken(userDetails);

        return TokenRefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime() / 1000)
                .build();
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeByToken(token);
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
