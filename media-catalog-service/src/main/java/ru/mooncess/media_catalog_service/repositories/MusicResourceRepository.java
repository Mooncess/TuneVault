package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.entities.MusicResource;

@Repository
public interface MusicResourceRepository extends JpaRepository<MusicResource, Long> {
}
