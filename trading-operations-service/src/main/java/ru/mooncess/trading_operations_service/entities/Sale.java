package ru.mooncess.trading_operations_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.trading_operations_service.domain.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime saleDate;
    @Email
    private String buyerEmail;
    private BigDecimal amountIncome;
    @Enumerated(EnumType.STRING)
    private SaleStatus status;
    @Column(name = "music_resource_id")
    @NotNull
    private Long musicResourceId;
}

