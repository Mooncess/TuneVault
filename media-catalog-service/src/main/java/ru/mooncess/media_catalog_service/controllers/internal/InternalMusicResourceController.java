package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.domain.MusicResourceSaleInfo;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;
import ru.mooncess.media_catalog_service.dto.MusicFileURI;
import ru.mooncess.media_catalog_service.services.MusicResourceService;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/internal/mcs/api/v1/music-resource")
@RequiredArgsConstructor
public class InternalMusicResourceController {
    @Value("${mcs.api.key}")
    private String secretApiKey;
    @Value("${download.path}")
    private String downloadPath;

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
    @GetMapping("/source-uri")
    ResponseEntity<String> getSourceURI (@RequestParam Long id,
                                         @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return musicResourceService.findById(id)
                .map(i -> ResponseEntity.ok(downloadPath + i.getSourceURI()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/update-files")
    ResponseEntity<Void> updateFilesOfMusicResource (@RequestParam Long id,
                                                     @RequestBody MusicFileURI musicFileURI,
                                                     @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        musicResourceService. updateURI(id, musicFileURI);
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

    @PutMapping("/delete-cover")
    ResponseEntity<String> deleteCover (@RequestParam Long id,
                                       @RequestParam String email,
                                       @RequestParam String defaultURI,
                                       @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (musicResourceService.isOwner(id, email)) {
            return ResponseEntity.ok(musicResourceService.deleteCover(id, defaultURI));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/strike")
    ResponseEntity<MusicFileURI> strikeAndDelete(@RequestParam Long id,
                                      @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            MusicFileURI musicFileURI = musicResourceService.strikeAndDelete(id);
            if (musicFileURI != null) return ResponseEntity.ok(musicFileURI);
            else return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/get-price-by-id")
    ResponseEntity<?> getPriceOfMusicResource (@RequestParam Long id,
                                                                  @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return musicResourceService.findById(id)
                .map(i -> {
                    if (i.getStatus().equals(MusicResourceStatus.AVAILABLE)) return ResponseEntity.ok(i.getPrice());
                    else return ResponseEntity.badRequest().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-mr-sale-info")
    ResponseEntity<MusicResourceSaleInfo> getMusicResourceSaleInfo(@RequestParam Long id,
                                                                   @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            return ResponseEntity.ok(musicResourceService.getMusicResourceSaleInfo(id));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
