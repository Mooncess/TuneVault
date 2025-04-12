package ru.mooncess.media_catalog_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mooncess.media_catalog_service.domain.AuthorId;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @EmbeddedId
    private AuthorId id;

    @ManyToOne
    @MapsId("producerId")
    @JoinColumn(name = "producer_id")
    private Producer producer;

    @ManyToOne
    @MapsId("musicResourceId")
    @JoinColumn(name = "music_resource_id")
    private MusicResource musicResource;
    private BigDecimal percentageOfSale;
}
