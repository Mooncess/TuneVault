package ru.mooncess.file_service.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MusicResourceInfo {
    private String sourceURI;
    @Value("${music.resource.default.logo.uri}")
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
