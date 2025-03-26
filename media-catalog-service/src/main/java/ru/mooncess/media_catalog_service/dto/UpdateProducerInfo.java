package ru.mooncess.media_catalog_service.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProducerInfo {
    private String nickname;
    private String about;
}
