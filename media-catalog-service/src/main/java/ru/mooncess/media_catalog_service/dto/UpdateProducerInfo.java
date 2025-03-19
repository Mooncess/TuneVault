package ru.mooncess.media_catalog_service.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProducerInfo {
    @Email
    private String email;
    private String nickname;
    private String about;
}
