package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.AuthorId;
import ru.mooncess.media_catalog_service.dto.AuthorInfo;
import ru.mooncess.media_catalog_service.entities.Author;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.exception.NoSuchProducerException;
import ru.mooncess.media_catalog_service.repositories.AuthorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final ProducerService producerService;
    private final AuthorRepository repository;
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
            authorId.setProducerId(author.getProducer().getId()); // Установите producerId
            authorId.setMusicResourceId(musicResource.getId()); // Установите musicResourceId

            author.setId(authorId);

            author.setMusicResource(musicResource);
            author.setRole(i.getRole());
            author.setPercentageOfSale(i.getPercentageOfSale());

            repository.save(author);
        }

        return true;
    }

    public boolean checkPercentageOfSale(List<AuthorInfo> authors) {
        long total = 0;
        for (AuthorInfo i : authors) {
            total += i.getPercentageOfSale();
        }

        return total == 100;
    }
}
