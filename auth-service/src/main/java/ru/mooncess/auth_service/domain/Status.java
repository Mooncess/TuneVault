package ru.mooncess.auth_service.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED"),
    ADMIN("ADMIN");

    private final String vale;
}
