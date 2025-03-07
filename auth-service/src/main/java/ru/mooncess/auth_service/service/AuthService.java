package ru.mooncess.auth_service.service;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Service;
import ru.mooncess.auth_service.config.SecurityConfig;
import ru.mooncess.auth_service.domain.*;
import ru.mooncess.auth_service.exception.AppError;
import ru.mooncess.auth_service.exception.AuthException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final User user = userService.findByUsername(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("The user was not found"));
        if (SecurityConfig.passwordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            redisService.save(user.getUsername(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Incorrect password");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = redisService.find(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.findByUsername(login)
                        .orElseThrow(() -> new AuthException("The user was not found"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = redisService.find(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.findByUsername(login)
                        .orElseThrow(() -> new AuthException("The user was not found"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                redisService.save(user.getUsername(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Invalid JWT token");
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationRequest registrationRequest) {
        if (userService.findByUsername(registrationRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError("A user with the specified email address already exists"), HttpStatus.BAD_REQUEST);
        }
        userService.createNewUser(registrationRequest);
        return ResponseEntity.ok().build();
    }
}
