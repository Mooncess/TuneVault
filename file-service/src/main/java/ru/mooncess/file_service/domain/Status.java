package ru.mooncess.file_service.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");
    private final String vale;
}
