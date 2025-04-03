package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;
import ru.mooncess.media_catalog_service.domain.enums.UserStatus;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;

import java.util.List;

@Repository
public interface MusicResourceRepository extends JpaRepository<MusicResource, Long>, JpaSpecificationExecutor<MusicResource> {
    List<MusicResource> findAllByProducer(Producer producer);
    List<MusicResource> findAllByProducerAndStatus(Producer producer, MusicResourceStatus musicResourceStatus);
}
