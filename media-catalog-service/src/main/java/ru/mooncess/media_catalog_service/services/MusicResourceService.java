package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.domain.MusicResourceStatus;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.mappers.MusicResourceMapper;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MusicResourceService {
    private final MusicResourceMapper mapper;
    private final MusicResourceRepository repository;
    private final AuthorService authorService;

    public boolean createNewMusicResource(MusicResourceInfo info) {
        if (!authorService.checkPercentageOfSale(info.getAuthors())) return false;

        MusicResource musicResource = mapper.map(info);
        musicResource.setStatus(MusicResourceStatus.AVAILABLE);
        musicResource.setCreationDate(LocalDate.now());

        repository.save(musicResource);

        return authorService.createAuthorsForMusicResource(musicResource, info.getAuthors());
    }
}
