package ru.mooncess.file_service.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicResourceBaseInfo {
    @NotNull
    private String name;
    private String key;
    private int bpm;
    private String genre;
    private double price;
    private String type;
    private List<AuthorInfo> authors;
}
