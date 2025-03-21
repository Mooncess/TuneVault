package ru.mooncess.file_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.file_service.domain.MusicResourceInfo;

@FeignClient(name = "MEDIA-CATALOG-SERVICE")
public interface MediaCatalogClient {
    @PostMapping("/internal/mcs/music-resource/api/v1/create")
    ResponseEntity<Void> createNewMusicResource(@RequestBody MusicResourceInfo musicResourceInfo,
                                                @RequestHeader("X-API-Key") String apiKey);
}
