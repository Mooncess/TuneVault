package ru.mooncess.media_catalog_service.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicResourceFilter {
    private String genre;
    private Double minPrice;
    private Double maxPrice;
    private String type;

    // Параметры пагинации и сортировки
    private int page = 0;
    private int size = 10;
    private String sort = "name,asc"; // Формат: "поле,направление"
}