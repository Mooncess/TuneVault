package ru.mooncess.media_catalog_service.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MusicResourceStatus {
    AVAILABLE("AVAILABLE"),
    DELETED("DELETED");
    private final String vale;
}

