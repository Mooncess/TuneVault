package ru.mooncess.auth_service.service;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.mooncess.auth_service.domain.JwtAuthentication;
import ru.mooncess.auth_service.domain.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        final List<String> roles = new ArrayList<>();
        final String role = claims.get("role", String.class);
        roles.add(role);
        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
