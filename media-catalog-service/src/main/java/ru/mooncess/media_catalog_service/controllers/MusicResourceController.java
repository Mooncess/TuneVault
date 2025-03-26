package ru.mooncess.media_catalog_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.dto.UpdateMusicResourceInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.filter.MusicResourceFilter;
import ru.mooncess.media_catalog_service.services.MusicResourceService;
import ru.mooncess.media_catalog_service.services.ProducerService;

import java.util.List;

@RestController
@RequestMapping("/mcs/api/v1/music-resource")
@RequiredArgsConstructor
public class MusicResourceController {
    private final MusicResourceService musicResourceService;
    private final ProducerService producerService;

    @GetMapping("/by-producer/{id}")
    ResponseEntity<List<MusicResource>> findMusicResourcesByProducerId(@PathVariable Long id) {
        return producerService.findById(id)
                .map(producer -> ResponseEntity.ok(musicResourceService.getProducersResources(id)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update-info/{id}")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> updateMusicResourceInfo(@PathVariable Long id,
                                              @RequestBody UpdateMusicResourceInfo updateMusicResourceInfo,
                                              Authentication authentication) {
        if (musicResourceService.isOwner(id, authentication.getName())) {
            musicResourceService.updateInfo(id, updateMusicResourceInfo);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/all")
    ResponseEntity<List<MusicResource>> findAll() {
        return ResponseEntity.ok(musicResourceService.findAll());
    }

    @GetMapping
    public ResponseEntity<Page<MusicResource>> getFilteredResources(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        MusicResourceFilter filter = new MusicResourceFilter();
        filter.setGenre(genre);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setType(type);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSort(sort);

        return ResponseEntity.ok(musicResourceService.findFiltered(filter));
    }
}
