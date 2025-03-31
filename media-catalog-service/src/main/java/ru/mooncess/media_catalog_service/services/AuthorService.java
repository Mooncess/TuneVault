package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.AuthorId;
import ru.mooncess.media_catalog_service.domain.enums.UserStatus;
import ru.mooncess.media_catalog_service.dto.AuthorInfo;
import ru.mooncess.media_catalog_service.entities.Author;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.entities.Sale;
import ru.mooncess.media_catalog_service.exception.NoSuchProducerException;
import ru.mooncess.media_catalog_service.repositories.AuthorRepository;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {
    private final ProducerService producerService;
    private final MusicResourceRepository musicResourceRepository;
    private final AuthorRepository authorRepository;
    private final RevenueService revenueService;
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
        BigDecimal total = new BigDecimal("0");
        boolean hasOwner = false;
        boolean allActive = true;

        for (AuthorInfo i : authors) {
            total = total.add(i.getPercentageOfSale());
            System.out.println(total + " " + i.getEmail() + " " + email);
            if (email.equals(i.getEmail())) hasOwner = true;
            if (producerService.findByEmail(i.getEmail())
                    .map(p -> p.getUserStatus() == UserStatus.BLOCKED)
                    .orElse(false)) {
                allActive = false;
                break;
            }
        }

        return hasOwner && allActive && total.compareTo(
                new BigDecimal("100")) == 0;
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

    public void increaseAuthorsBalance(BigDecimal amountIncome, MusicResource musicResource, Sale sale) {
        List<Author> authors = findAuthorsOfResource(musicResource.getId());
        BigDecimal remainingAmount = amountIncome;

        for (int i = 0; i < authors.size() - 1; i++) {
            Author author = authors.get(i);
            BigDecimal authorAmount = amountIncome.multiply(author.getPercentageOfSale())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            author.getProducer().setBalance((author.getProducer().getBalance().add(authorAmount)));
            authorRepository.save(author);
            revenueService.createRevenue(authorAmount, sale, author.getProducer());
            remainingAmount = remainingAmount.subtract(authorAmount);
        }

        Author lastAuthor = authors.get(authors.size() - 1);
        lastAuthor.getProducer().setBalance(lastAuthor.getProducer().getBalance().add(remainingAmount));
        authorRepository.save(lastAuthor);
        revenueService.createRevenue(remainingAmount, sale, lastAuthor.getProducer());

        log.info("Распределен доход {} для ресурса {} между {} авторами",
                amountIncome, musicResource.getId(), authors.size());
    }
}
