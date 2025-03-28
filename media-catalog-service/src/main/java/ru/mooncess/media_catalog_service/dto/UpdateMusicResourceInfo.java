package ru.mooncess.media_catalog_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateMusicResourceInfo {
    private String name;
    private String key;
    private Integer bpm;
    private String genre;
    private BigDecimal price;
}
