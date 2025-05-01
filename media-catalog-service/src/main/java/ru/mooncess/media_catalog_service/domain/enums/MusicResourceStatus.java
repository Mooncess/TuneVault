package ru.mooncess.media_catalog_service.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MusicResourceStatus {
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE"),
    BLOCKED("BLOCKED"),
    DELETED("DELETED");
    private final String vale;
}

