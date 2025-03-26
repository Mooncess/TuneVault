package ru.mooncess.media_catalog_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMusicResourceInfo {
    private String name;
    private String key;
    private Integer bpm;
    private String genre;
    private Double price;
}
