package ru.mooncess.media_catalog_service.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED"),
    ADMIN("ADMIN");
    private final String vale;
}
