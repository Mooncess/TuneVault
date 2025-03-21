package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.dto.AuthorInfo;
import ru.mooncess.media_catalog_service.services.MusicResourceService;

@RestController
@RequestMapping("/internal/mcs/music-resource/api/v1")
@RequiredArgsConstructor
public class MusicResourceController {
    private final MusicResourceService service;
    @PostMapping("/create")
    ResponseEntity<Void> createNewMusicResource(@RequestBody MusicResourceInfo musicResourceInfo,
                                                @RequestHeader("X-API-Key") String apiKey) {
        System.out.println("Запрос на создание ресурса");

        for (AuthorInfo i : musicResourceInfo.getAuthors()) {
            System.out.println(i.getEmail());
        }

        if (service.createNewMusicResource(musicResourceInfo))
            return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
