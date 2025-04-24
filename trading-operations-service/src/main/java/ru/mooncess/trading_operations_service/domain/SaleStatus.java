package ru.mooncess.trading_operations_service.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SaleStatus {
    CREATED("CREATED"),
    PAID_FOR("PAID_FOR"),
    CANCELED("CANCELED");
    private final String vale;
}
