package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.AuthorId;
import ru.mooncess.media_catalog_service.domain.UserStatus;
import ru.mooncess.media_catalog_service.dto.AuthorInfo;
import ru.mooncess.media_catalog_service.entities.Author;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.exception.NoSuchProducerException;
import ru.mooncess.media_catalog_service.repositories.AuthorRepository;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final ProducerService producerService;
    private final MusicResourceRepository musicResourceRepository;
    private final AuthorRepository authorRepository;
    public boolean createAuthorsForMusicResource(MusicResource musicResource, List<AuthorInfo> authorList) {
        for (AuthorInfo i : authorList) {
            Author author = new Author();

            try {
                author.setProducer(producerService.findByEmail(i.getEmail()).orElseThrow(
                        () -> new NoSuchProducerException("Producer not found with email: " + i.getEmail())
                ));
            }
            catch (NoSuchProducerException e) {
                System.err.println(e.getMessage());
                return false;
            }

            AuthorId authorId = new AuthorId();
            authorId.setProducerId(author.getProducer().getId());
            authorId.setMusicResourceId(musicResource.getId());

            author.setId(authorId);

            author.setMusicResource(musicResource);
            author.setRole(i.getRole());
            author.setPercentageOfSale(i.getPercentageOfSale());

            authorRepository.save(author);
        }

        return true;
    }

    public boolean checkAuthors(String email, List<AuthorInfo> authors) {
        long total = 0;
        boolean hasOwner = false;
        boolean allActive = true;

        for (AuthorInfo i : authors) {
            total += i.getPercentageOfSale();
            if (email.equals(i.getEmail())) hasOwner = true;
            if (producerService.findByEmail(i.getEmail())
                    .map(p -> p.getUserStatus() == UserStatus.BLOCKED)
                    .orElse(false)) {
                allActive = false;
                break;
            }
        }

        return hasOwner && allActive && total == 100;
    }

    public Optional<Producer> findProducerById(Long id) {
        return producerService.findById(id);
    }

    public List<Author> findAuthorsOfResource(Long id) {
        return musicResourceRepository.findById(id)
                .map(authorRepository::findAllByMusicResource)
                .orElseGet(Collections::emptyList);
    }

    public Optional<Producer> findProducerByEmail(String email) {
        return producerService.findByEmail(email);
    }
}
