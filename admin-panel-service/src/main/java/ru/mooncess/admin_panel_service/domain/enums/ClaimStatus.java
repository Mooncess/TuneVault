package ru.mooncess.admin_panel_service.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClaimStatus {
    NOT_PROCESSED("NOT_PROCESSED"),
    ACCEPTED("ACCEPTED"),
    REVIEWED("REVIEWED");
    private final String vale;
}