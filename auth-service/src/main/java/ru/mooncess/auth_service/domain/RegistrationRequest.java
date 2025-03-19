package ru.mooncess.auth_service.domain;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationRequest {
    @Email
    private String username;
    private String password;
    private String nickname;
}
