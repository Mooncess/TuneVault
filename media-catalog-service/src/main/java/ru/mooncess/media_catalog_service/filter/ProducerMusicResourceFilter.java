package ru.mooncess.media_catalog_service.filter;

import lombok.Getter;
import lombok.Setter;
import ru.mooncess.media_catalog_service.entities.Producer;

@Getter
@Setter
public class ProducerMusicResourceFilter {
    private Producer producer;
    private String genre;
    private Double minPrice;
    private Double maxPrice;
    private String type;

    private int page = 0;
    private int size = 10;
    private String sort = "name,asc";
}
