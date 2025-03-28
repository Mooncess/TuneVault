package ru.mooncess.file_service.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MusicResourceBaseInfo {
    @NotNull
    private String name;
    private String key;
    private int bpm;
    private String genre;
    private BigDecimal price;
    private String type;
    private List<AuthorInfo> authors;
}
