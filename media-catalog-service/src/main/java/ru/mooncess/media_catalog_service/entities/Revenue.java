package ru.mooncess.media_catalog_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @NotNull
    private Sale sale;
    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    @NotNull
    private Producer producer;
}
