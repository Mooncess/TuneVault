package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.domain.AuthorId;
import ru.mooncess.media_catalog_service.entities.Author;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, AuthorId> {
    List<MusicResource> findAllMusicResourcesByProducer(Producer producer);
    List<Author> findAllByMusicResource(MusicResource musicResource);
}
