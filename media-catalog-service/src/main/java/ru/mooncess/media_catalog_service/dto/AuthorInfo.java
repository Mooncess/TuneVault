package ru.mooncess.media_catalog_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AuthorInfo {
    private String email;
    private String role;
    private BigDecimal percentageOfSale;
}
