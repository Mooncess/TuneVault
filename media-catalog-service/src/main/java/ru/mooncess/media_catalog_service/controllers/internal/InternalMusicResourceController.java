package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.dto.MusicFileURI;
import ru.mooncess.media_catalog_service.services.MusicResourceService;

@RestController
@RequestMapping("/internal/mcs/api/v1/music-resource")
@RequiredArgsConstructor
public class InternalMusicResourceController {
    @Value("${mcs.api.key}")
    private String secretApiKey;

    private final MusicResourceService musicResourceService;
    @PostMapping("/create")
    ResponseEntity<Void> createNewMusicResource(@RequestBody MusicResourceInfo musicResourceInfo,
                                                @RequestParam String email,
                                                @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (musicResourceService.createNewMusicResource(musicResourceInfo, email))
            return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    @GetMapping("/check-owner")
    ResponseEntity<MusicFileURI> checkOwner (@RequestParam Long id,
                                             @RequestParam String email,
                                             @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (musicResourceService.isOwner(id, email)) {
            return ResponseEntity.ok(musicResourceService.getFilesURI(id));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    @PutMapping("/update-files")
    ResponseEntity<Void> updateFilesOfMusicResource (@RequestParam Long id,
                                                     @RequestBody MusicFileURI musicFileURI,
                                                     @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        musicResourceService.updateURI(id, musicFileURI);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/delete-demo")
    ResponseEntity<String> deleteDemo (@RequestParam Long id,
                                  @RequestParam String email,
                                  @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (musicResourceService.isOwner(id, email)) {
            return ResponseEntity.ok(musicResourceService.deleteDemo(id));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/delete-logo")
    ResponseEntity<String> deleteLogo (@RequestParam Long id,
                                       @RequestParam String email,
                                       @RequestParam String defaultURI,
                                       @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (musicResourceService.isOwner(id, email)) {
            return ResponseEntity.ok(musicResourceService.deleteLogo(id, defaultURI));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
