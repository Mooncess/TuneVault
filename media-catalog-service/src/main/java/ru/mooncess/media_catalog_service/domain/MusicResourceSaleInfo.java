package ru.mooncess.media_catalog_service.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicResourceSaleInfo {
    private String sourceURI;
    private List<AuthorSaleInfo> authorInfoList;
}

