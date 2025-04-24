package ru.mooncess.media_catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TRADING-OPERATIONS-SERVICE")
public interface TradingOperationServClient {
    @PostMapping("/internal/tos/api/v1/producer-balance/create")
    ResponseEntity<Long> createNewProducerBalance(@RequestParam String email,
                                                  @RequestHeader("X-API-Key") String apiKey);
}
