package ru.mooncess.media_catalog_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class AuthorId implements Serializable {

    @Column(name = "producer_id")
    private Long producerId;

    @Column(name = "music_resource_id")
    private Long musicResourceId;
}
