package ru.mooncess.admin_panel_service.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mooncess.admin_panel_service.domain.JwtInfo;

import javax.crypto.SecretKey;

@Slf4j
@Component
public final class JwtChecker {
    private final SecretKey jwtSecret;

    public JwtChecker(@Value("${jwt.secret}") String secret) {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public JwtInfo checkToken(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String token = getTokenFromRequest(httpRequest);

        if (token != null && validateToken(token)) {
            final Claims claims = getClaims(token);
            return createJwtInfoToken(claims);
        }

        return null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static JwtInfo createJwtInfoToken(Claims claims) {
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setRole(claims.get("role", String.class));
        jwtInfo.setUsername(claims.get("sub", String.class));
        return jwtInfo;
    }

    public boolean validateToken(@NonNull String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public Claims getClaims(@NonNull String token) {
        return getClaims(token, jwtSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
