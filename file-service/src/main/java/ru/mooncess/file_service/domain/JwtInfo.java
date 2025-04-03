package ru.mooncess.file_service.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtInfo {
    private String username;
    private String role;
}
