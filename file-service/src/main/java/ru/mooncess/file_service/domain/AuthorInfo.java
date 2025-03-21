package ru.mooncess.file_service.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorInfo {
    private String email;
    private String role;
    private double percentageOfSale;
}
