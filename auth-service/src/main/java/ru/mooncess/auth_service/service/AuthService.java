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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    public JwtResponse adminLogin(@NonNull JwtRequest authRequest) throws AuthException {
        System.out.println("HERE " + authRequest.getLogin());
        final User user = userService.findByUsername(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("The user was not found"));
        if (user.getStatus().equals(Status.INACTIVE) || user.getStatus().equals(Status.BLOCKED) || !user.getRole().equals(R.ole.ADMIN)) throw new AuthException("Access to the account is prohibited");
        if (SecurityConfig.passwordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            System.out.println(user.getStatus());
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            redisService.save(user.getUsername(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Incorrect password");
        }
    }

    public JwtResponse userLogin(@NonNull JwtRequest authRequest) throws AuthException {
        final User user = userService.findByUsername(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("The user was not found"));
        if (user.getStatus().equals(Status.INACTIVE) || user.getStatus().equals(Status.BLOCKED) || user.getStatus().equals(Status.ADMIN)) throw new AuthException("Access to the account is prohibited");
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

    public Optional<User> createNewUser(@RequestBody RegistrationRequest registrationRequest, long id) {
        return Optional.of(userService.createNewUser(registrationRequest, id));
    }

    public JwtResponse updateUser(User user, String oldUsername) {
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        redisService.delete(oldUsername);
        redisService.save(user.getUsername(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    public void disableProfile(String username) {
        userService.disable(username);
        redisService.delete(username);
    }

    public ResponseEntity<?> deleteUser(String username) {
        var user = userService.findByUsername(username);
        if (user.isPresent()) {
            try {
                ResponseEntity<?> status = userService.deleteUserRequest(user.get().getId());
                if (status.getStatusCode() == HttpStatus.NO_CONTENT) {
                    userService.delete(user.get());
                    redisService.delete(user.get().getUsername());
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
