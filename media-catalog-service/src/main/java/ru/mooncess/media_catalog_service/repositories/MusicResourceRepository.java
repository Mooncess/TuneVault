package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;

import java.util.List;

@Repository
public interface MusicResourceRepository extends JpaRepository<MusicResource, Long>, JpaSpecificationExecutor<MusicResource> {
    @Query("SELECT mr FROM Author a JOIN a.musicResource mr WHERE a.producer = :producer")
    List<MusicResource> findAllByProducer(@Param("producer")Producer producer);
}
