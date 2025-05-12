package ru.mooncess.admin_panel_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.admin_panel_service.dto.MusicFileURI;

@FeignClient(name = "MEDIA-CATALOG-SERVICE")
public interface MediaCatalogClient {
    @GetMapping("/internal/mcs/api/v1/music-resource/source-uri")
    ResponseEntity<String> getMusicResourceSource (@RequestParam Long id,
                                                   @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/internal/mcs/api/v1/music-resource/strike")
    ResponseEntity<MusicFileURI> strikeAndDelete(@RequestParam Long id,
                                                 @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/internal/mcs/api/v1/producer/block")
    ResponseEntity<?> blockProducer(@RequestParam Long producerId,
                                    @RequestHeader("X-API-Key") String apiKey);
}
