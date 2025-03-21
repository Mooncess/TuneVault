package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.domain.AuthorId;
import ru.mooncess.media_catalog_service.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, AuthorId> {
}
