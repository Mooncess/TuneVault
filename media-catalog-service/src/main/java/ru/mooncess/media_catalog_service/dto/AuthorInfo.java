package ru.mooncess.media_catalog_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorInfo {
    private String email;
    private String role;
    private double percentageOfSale;
}
