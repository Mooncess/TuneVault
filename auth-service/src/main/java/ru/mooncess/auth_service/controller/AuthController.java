package ru.mooncess.auth_service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import ru.mooncess.auth_service.domain.JwtRequest;
import ru.mooncess.auth_service.domain.JwtResponse;
import ru.mooncess.auth_service.domain.RegistrationRequest;
import ru.mooncess.auth_service.exception.AppError;
import ru.mooncess.auth_service.service.AuthService;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(authRequest);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");
        headers.add("Set-Cookie", "refresh=" + token.getRefreshToken() + "; Path=/api/v1; Max-Age=3600; HttpOnly");

        return ResponseEntity.ok()
                .headers(headers)
                .body(token);
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("access", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setPath("/api/v1");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationRequest registrationRequest) {
        if (authService.createNewUser(registrationRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return new ResponseEntity<>(new AppError("A user with the specified email address already exists"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(HttpServletRequest servletRequest) {
        String refreshToken = getCookieValue(servletRequest);

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        final JwtResponse token = authService.getAccessToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");

        return ResponseEntity.ok()
                .headers(headers)
                .body(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(HttpServletRequest servletRequest) {
        String refreshToken = getCookieValue(servletRequest);
        System.out.println(refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        final JwtResponse token = authService.refresh(refreshToken);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");
        headers.add("Set-Cookie", "refresh=" + token.getRefreshToken() + "; Path=/api/v1; Max-Age=3600; HttpOnly");

        return ResponseEntity.ok()
                .headers(headers)
                .body(token);
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    System.out.println(cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
