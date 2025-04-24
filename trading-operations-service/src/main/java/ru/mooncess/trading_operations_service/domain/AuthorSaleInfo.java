package ru.mooncess.trading_operations_service.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AuthorSaleInfo {
    private Long producerId;
    private BigDecimal percentageOfSale;
}
