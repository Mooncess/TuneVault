package ru.mooncess.media_catalog_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.MusicResourceStatus;

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
    private String sourceURI;
    private String logoURI;
    private String demoURI;
    private LocalDate creationDate;
    private String name;
    private String key;
    private int bpm;
    private String genre;
    private double price;
    private String type;
    @Enumerated(EnumType.STRING)
    private MusicResourceStatus status;
}
