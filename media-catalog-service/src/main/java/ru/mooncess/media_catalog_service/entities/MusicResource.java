package ru.mooncess.media_catalog_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicResource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @JsonIgnore
    private String sourceURI;
    private String coverURI;
    private String demoURI;
    private LocalDate creationDate;
    private String name;
    private String key;
    private int bpm;
    private String genre;
    private BigDecimal price;
    private String type;
    @Enumerated(EnumType.STRING)
    private MusicResourceStatus status;
    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    @NotNull
    private Producer producer;
}
