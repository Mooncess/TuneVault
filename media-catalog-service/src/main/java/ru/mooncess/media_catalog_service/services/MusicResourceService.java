package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mooncess.media_catalog_service.domain.AuthorSaleInfo;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.domain.MusicResourceSaleInfo;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;
import ru.mooncess.media_catalog_service.dto.MusicFileURI;
import ru.mooncess.media_catalog_service.dto.UpdateMusicResourceInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.filter.MusicResourceFilter;
import ru.mooncess.media_catalog_service.filter.MusicResourceSpecifications;
import ru.mooncess.media_catalog_service.mappers.MusicResourceMapper;
import ru.mooncess.media_catalog_service.repositories.MusicResourceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicResourceService {
    private final MusicResourceMapper mapper;
    private final MusicResourceRepository repository;
    private final AuthorService authorService;

    @Transactional
    public boolean createNewMusicResource(MusicResourceInfo info, String email) {
        if (!authorService.checkAuthors(email, info.getAuthors())) return false;

        Producer producer = authorService.findProducerByEmail(email)
                .orElse(null);

        if (producer == null) {
            return false;
        }

        MusicResource musicResource = mapper.map(info);
        musicResource.setStatus(MusicResourceStatus.AVAILABLE);
        musicResource.setCreationDate(LocalDate.now());
        musicResource.setProducer(producer);

        repository.save(musicResource);

        return authorService.createAuthorsForMusicResource(musicResource, info.getAuthors());
    }

    public List<MusicResource> findResourcesByProducer(Producer producer) {
        return repository.findAllByProducer(producer);
    }

    public List<MusicResource> getAvailableProducersResources(Long id) {
        var p = authorService.findProducerById(id);

        if (p.isPresent()) {
            return repository.findAllByProducerAndStatus(p.get(), MusicResourceStatus.AVAILABLE);
        }

        return Collections.emptyList();
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
                    musicFileURI.setCoverURI(musicResource.getCoverURI());
                    musicFileURI.setSourceURI(musicResource.getSourceURI());
                    return musicFileURI;
                })
                .orElseThrow(() -> new RuntimeException("MusicResource not found with id: " + id));
    }

    public boolean isOwner(Long id, String email) {
        var mr = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MusicResource not found with id: " + id));
        return mr.getProducer().getEmail().equals(email);
    }

    public void updateURI(Long id, MusicFileURI musicFileURI) {
        repository.findById(id).map(i -> {
            if (musicFileURI.getCoverURI() != null) i.setCoverURI(musicFileURI.getCoverURI());
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

    public String deleteCover(Long id, String defaultURI) {
        Optional<MusicResource> mr = repository.findById(id);
        if (mr.isPresent()) {
            String temp = mr.get().getCoverURI();
            mr.get().setCoverURI(defaultURI);
            repository.save(mr.get());
            return temp;
        }
        return "";
    }

    public Optional<MusicResource> findById(Long id) {
        return repository.findById(id);
    }

    public MusicFileURI strikeAndDelete(Long id) {
        var mr = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music Resource not found with id: " + id));
        if (mr.getStatus().equals(MusicResourceStatus.BLOCKED)) return null;
        mr.setStatus(MusicResourceStatus.BLOCKED);
        authorService.strike(mr.getProducer());
        repository.save(mr);

        MusicFileURI musicFileURI = new MusicFileURI();
        musicFileURI.setCoverURI(mr.getCoverURI());
        musicFileURI.setDemoURI(mr.getDemoURI());
        musicFileURI.setSourceURI(mr.getSourceURI());

        return musicFileURI;
    }

    public void unavailableMusicResource(Long id) {
        var musicResource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music Resource not found with id: " + id));

        musicResource.setStatus(MusicResourceStatus.UNAVAILABLE);
        repository.save(musicResource);
    }

    public List<MusicResource> getProducersResources(Producer producer) {
        return repository.findAllByProducer(producer)
                .stream().filter(i -> !i.getStatus().equals(MusicResourceStatus.BLOCKED) && !i.getStatus().equals(MusicResourceStatus.DELETED))
                .toList();
    }

    public MusicResourceSaleInfo getMusicResourceSaleInfo(Long id) {
        MusicResourceSaleInfo info = new MusicResourceSaleInfo();

        MusicResource mr = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music Resource Not found with ID: " + id));

        List<AuthorSaleInfo> list = new ArrayList<>();

        info.setSourceURI(mr.getSourceURI());
        authorService.findAuthorsOfResource(id)
                .forEach(i -> {
                    AuthorSaleInfo authorSaleInfo = new AuthorSaleInfo();
                    authorSaleInfo.setProducerId(i.getProducer().getId());
                    authorSaleInfo.setPercentageOfSale(i.getPercentageOfSale());
                    list.add(authorSaleInfo);
                });

        info.setAuthorInfoList(list);
        return info;
    }

    public void availableMusicResource(Long id) {
        var musicResource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music Resource not found with id: " + id));

        musicResource.setStatus(MusicResourceStatus.AVAILABLE);
        repository.save(musicResource);
    }

    public void deleteMusicResource(Long id) {
        var musicResource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music Resource not found with id: " + id));

        musicResource.setStatus(MusicResourceStatus.DELETED);
        repository.save(musicResource);
    }
}
