package com.banking.springboot.security.services;

import com.banking.springboot.auth.User;
import com.banking.springboot.auth.UserRepository;
import com.banking.springboot.entity.RefreshToken;
import com.banking.springboot.exceptions.TokenRefreshException;
import com.banking.springboot.exceptions.UserDoesNotExistException;
import com.banking.springboot.repository.RefreshTokenRepository;
import com.banking.springboot.security.jwt.JwtUtils;
import com.banking.springboot.security.jwt.TokenRefreshRequest;
import com.banking.springboot.security.jwt.TokenRefreshResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.token.refreshExpiration.in.ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils utils;

    public RefreshToken findByToken(String token) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(token);
        if(existingToken != null) {
            return existingToken;
        } else {
            throw new TokenRefreshException(token, "Refresh token not found.");
        }
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        RefreshToken token = refreshTokenRepository.findByToken(requestRefreshToken);
        if(token != null) {
            verifyExpiration(token);
            User user = token.getUser();
            String newToken = utils.generateTokenFromUsername(user.getUsername());
            return new TokenRefreshResponse(newToken, requestRefreshToken);
        } else {
            throw new TokenRefreshException(requestRefreshToken, "No refresh token found.");
        }
    }

    public RefreshToken createRefreshToken(Integer userId) throws UserDoesNotExistException {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findUserById(userId);
        if(user != null) {
            refreshToken.setUser(user);
        } else {
            throw new UserDoesNotExistException("User with id " + userId + " not found.");
        }

        refreshToken.setExpiryDate(Instant.now().plusMillis(86400000));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) throws TokenRefreshException {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please sign in again.");
        }
        return token;
    }

    public void deleteByUserId() throws IllegalArgumentException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userDetails.getId());
        if(refreshToken != null) {
            refreshTokenRepository.delete(refreshToken);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }
}
