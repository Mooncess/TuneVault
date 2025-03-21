package ru.mooncess.media_catalog_service.domain;

import lombok.Getter;
import lombok.Setter;
import ru.mooncess.media_catalog_service.dto.AuthorInfo;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MusicResourceInfo {
    private String sourceURI;
    private String logoURI;
    private String demoURI;
    private String name;
    private String key;
    private int bpm;
    private String genre;
    private double price;
    private String type;
    private List<AuthorInfo> authors;
}
