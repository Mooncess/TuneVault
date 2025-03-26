package ru.mooncess.file_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.file_service.domain.MusicFileURI;
import ru.mooncess.file_service.domain.MusicResourceInfo;

@FeignClient(name = "MEDIA-CATALOG-SERVICE")
public interface MediaCatalogClient {
    @PostMapping("/internal/mcs/api/v1/music-resource/create")
    ResponseEntity<Void> createNewMusicResource(@RequestBody MusicResourceInfo musicResourceInfo,
                                                @RequestParam String email,
                                                @RequestHeader("X-API-Key") String apiKey);
    @PutMapping("/internal/mcs/api/v1/music-resource/update-files")
    ResponseEntity<Void> updateFilesOfMusicResource (@RequestParam Long id,
                                                     @RequestBody MusicFileURI musicFileURI,
                                                     @RequestHeader("X-API-Key") String apiKey);
    @GetMapping("/internal/mcs/api/v1/music-resource/check-owner")
    ResponseEntity<MusicFileURI> checkOwner (@RequestParam Long id,
                                     @RequestParam String email,
                                     @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/internal/mcs/api/v1/music-resource/delete-demo")
    ResponseEntity<String> deleteDemo (@RequestParam Long id,
                                  @RequestParam String email,
                                  @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/internal/mcs/api/v1/music-resource/delete-logo")
    ResponseEntity<String> deleteLogo (@RequestParam Long id,
                                     @RequestParam String email,
                                     @RequestParam String defaultURI,
                                     @RequestHeader("X-API-Key") String apiKey);
}
