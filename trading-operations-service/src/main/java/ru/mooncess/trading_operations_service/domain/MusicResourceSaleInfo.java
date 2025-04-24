package ru.mooncess.trading_operations_service.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicResourceSaleInfo {
    private String sourceURI;
    private List<AuthorSaleInfo> authorInfoList;
}
