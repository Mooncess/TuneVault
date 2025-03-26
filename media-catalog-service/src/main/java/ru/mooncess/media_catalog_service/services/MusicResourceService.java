package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.domain.MusicResourceStatus;
import ru.mooncess.media_catalog_service.dto.MusicFileURI;
import ru.mooncess.media_catalog_service.dto.UpdateMusicResourceInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.filter.MusicResourceFilter;
import ru.mooncess.media_catalog_service.filter.MusicResourceSpecifications;
import ru.mooncess.media_catalog_service.mappers.MusicResourceMapper;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicResourceService {
    private final MusicResourceMapper mapper;
    private final MusicResourceRepository repository;
    private final AuthorService authorService;

    public boolean createNewMusicResource(MusicResourceInfo info, String email) {
        if (!authorService.checkAuthors(email, info.getAuthors())) return false;

        MusicResource musicResource = mapper.map(info);
        musicResource.setStatus(MusicResourceStatus.AVAILABLE);
        musicResource.setCreationDate(LocalDate.now());

        repository.save(musicResource);

        return authorService.createAuthorsForMusicResource(musicResource, info.getAuthors());
    }

    public List<MusicResource> findResourcesByProducer(Producer producer) {
        return repository.findAllByProducer(producer);
    }

    public List<MusicResource> getProducersResources(Long id) {
        Optional<Producer> p = authorService.findProducerById(id);
        return p.map(this::findResourcesByProducer).orElse(Collections.emptyList());
    }

    public List<MusicResource> findAll() {
        return repository.findAll();
    }

    public Page<MusicResource> findFiltered(MusicResourceFilter filter) {
        Specification<MusicResource> spec = MusicResourceSpecifications.buildSpecification(filter);

        String[] sortParams = filter.getSort().split(",");
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortParams[1]),
                sortParams[0]
        );

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        return repository.findAll(spec, pageable);
    }

    public MusicFileURI getFilesURI(Long id) {
        return repository.findById(id)
                .map(musicResource -> {
                    MusicFileURI musicFileURI = new MusicFileURI();
                    musicFileURI.setDemoURI(musicResource.getDemoURI());
                    musicFileURI.setLogoURI(musicResource.getLogoURI());
                    musicFileURI.setSourceURI(musicResource.getSourceURI());
                    return musicFileURI;
                })
                .orElseThrow(() -> new RuntimeException("MusicResource not found with id: " + id));
    }

    public boolean isOwner(Long id, String email) {
        return authorService.findAuthorsOfResource(id)
                .stream().anyMatch(a -> a.getProducer().getEmail().equals(email));
    }

    public void updateURI(Long id, MusicFileURI musicFileURI) {
        repository.findById(id).map(i -> {
            if (musicFileURI.getLogoURI() != null) i.setLogoURI(musicFileURI.getLogoURI());
            if (musicFileURI.getDemoURI() != null) i.setDemoURI(musicFileURI.getDemoURI());
            if (musicFileURI.getSourceURI() != null) i.setSourceURI(musicFileURI.getSourceURI());
            return repository.save(i);
        });
    }

    public void updateInfo(Long id, UpdateMusicResourceInfo updateMusicResourceInfo) {
        repository.findById(id).map(i -> {
            if (updateMusicResourceInfo.getName() != null) i.setName(updateMusicResourceInfo.getName());
            if (updateMusicResourceInfo.getKey() != null) i.setKey(updateMusicResourceInfo.getKey());
            if (updateMusicResourceInfo.getBpm() != null) i.setBpm(updateMusicResourceInfo.getBpm());
            if (updateMusicResourceInfo.getGenre() != null) i.setGenre(updateMusicResourceInfo.getGenre());
            if (updateMusicResourceInfo.getPrice() != null) i.setPrice(updateMusicResourceInfo.getPrice());
            return  repository.save(i);
        });
    }

    public String deleteDemo(Long id) {
        Optional<MusicResource> mr = repository.findById(id);
        if (mr.isPresent()) {
            String temp = mr.get().getDemoURI();
            mr.get().setDemoURI(null);
            repository.save(mr.get());
            return temp;
        }
        return "";
    }

    public String deleteLogo(Long id, String defaultURI) {
        Optional<MusicResource> mr = repository.findById(id);
        if (mr.isPresent()) {
            String temp = mr.get().getLogoURI();
            mr.get().setLogoURI(defaultURI);
            repository.save(mr.get());
            return temp;
        }
        return "";
    }
}
