package ru.mooncess.media_catalog_service.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SaleStatus {
    CREATED("CREATED"),
    PAID_FOR("PAID_FOR"),
    CANCELED("CANCELED");
    private final String vale;
}
