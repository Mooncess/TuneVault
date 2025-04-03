package ru.mooncess.admin_panel_service.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtInfo {
    private String username;
    private String role;
}
