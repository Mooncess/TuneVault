package ru.mooncess.auth_service.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class JwtRequest {
    private String login;
    private String password;

}