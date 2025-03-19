package ru.mooncess.auth_service.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED");

    private final String vale;
}
