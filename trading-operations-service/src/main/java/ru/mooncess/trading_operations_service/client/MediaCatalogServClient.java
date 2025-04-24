package ru.mooncess.trading_operations_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.trading_operations_service.domain.MusicResourceSaleInfo;

import java.math.BigDecimal;

@FeignClient(name = "MEDIA-CATALOG-SERVICE")
public interface MediaCatalogServClient {
    @GetMapping("/internal/mcs/api/v1/music-resource/get-price-by-id")
    ResponseEntity<?> getPriceOfMusicResource(@RequestParam Long id,
                                              @RequestHeader("X-API-Key") String apiKey);

    @GetMapping("/internal/mcs/api/v1/music-resource/get-mr-sale-info")
    ResponseEntity<MusicResourceSaleInfo> getMusicResourceSaleInfo(@RequestParam Long id,
                                                                   @RequestHeader("X-API-Key") String apiKey);

    @GetMapping("/internal/mcs/api/v1/producer/get-producer-id-by-email")
    ResponseEntity<Long> getProducerIdByEmail(@RequestParam String email,
                                              @RequestHeader("X-API-Key") String apiKey);

}
