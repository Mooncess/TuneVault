package ru.mooncess.auth_service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.mooncess.auth_service.domain.*;
import ru.mooncess.auth_service.exception.AppError;
import ru.mooncess.auth_service.service.AuthService;
import ru.mooncess.auth_service.service.UserService;
import ru.mooncess.auth_service.clients.MediaCatalogClient;

@RestController
@RequestMapping("/auth/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final MediaCatalogClient mediaCatalogClient;
    @Value("${secret.api.key}")
    private String secretApiKey;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(authRequest);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");
        headers.add("Set-Cookie", "refresh=" + token.getRefreshToken() + "; Path=/auth/api; Max-Age=3600; HttpOnly");

        return ResponseEntity.ok()
                .headers(headers)
                .body(token);
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        var accessCookie = new Cookie("access", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setPath("/auth/api");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody @Validated RegistrationRequest registrationRequest) {
        ProducerInfo producerInfo = new ProducerInfo();
        producerInfo.setNickname(registrationRequest.getNickname());
        producerInfo.setEmail(registrationRequest.getUsername());

        try {
            ResponseEntity<Long> response = mediaCatalogClient.registrationNewProducer(producerInfo, secretApiKey);
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(new AppError("A user with the specified email address already exists"), HttpStatus.BAD_REQUEST);
            }
            authService.createNewUser(registrationRequest, response.getBody());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError("The service is temporarily unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PutMapping("/user/update")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestParam(required = false) @Email @Validated String email,
                                        @RequestParam(required = false) String password,
                                        Authentication authentication) {
        ResponseEntity<?> response = userService.updateUser(email, password, authentication);
        if (response.getStatusCode() == HttpStatus.OK) {
            final JwtResponse token;
            if (email != null) token = authService.updateUser(userService.findByUsername(email).get(), authentication.getName());
            else token = authService.updateUser(userService.findByUsername(authentication.getName()).get(), authentication.getName());

            HttpHeaders headers = new HttpHeaders();

            headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");
            headers.add("Set-Cookie", "refresh=" + token.getRefreshToken() + "; Path=/auth/api; Max-Age=3600; HttpOnly");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(token);
        }
        return response;
    }

    @DeleteMapping("/user/delete")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteUser(Authentication authentication, HttpServletResponse response) {
        var responseEntity = authService.deleteUser(authentication.getName());
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            return logout(response);
        }
        return responseEntity;
    }
    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(HttpServletRequest servletRequest,
                                                         Authentication authentication) {
        String refreshToken = getCookieValue(servletRequest);

        System.out.println(refreshToken);

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

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        final JwtResponse token = authService.refresh(refreshToken);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Set-Cookie", "access=" + token.getAccessToken() + "; Path=/; Max-Age=3600; HttpOnly");
        headers.add("Set-Cookie", "refresh=" + token.getRefreshToken() + "; Path=/auth/api; Max-Age=3600; HttpOnly");

        return ResponseEntity.ok()
                .headers(headers)
                .body(token);
    }

    @PutMapping("/disable")
    public ResponseEntity<JwtResponse> disableProfile(HttpServletRequest servletRequest,
                                                      HttpServletResponse response,
                                                      Authentication authentication) {
        authService.disableProfile(authentication.getName());

        var accessCookie = new Cookie("access", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setPath("/auth/api");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.noContent().build();
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName());
                if (cookie.getName().equals("refresh")) {
                    System.out.println(cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
