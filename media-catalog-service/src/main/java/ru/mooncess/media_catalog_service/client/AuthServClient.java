package ru.mooncess.media_catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthServClient {
    @PutMapping("/auth/api/v1/admin/strike")
    ResponseEntity<Boolean> strikeAndDelete(@RequestParam Long id,
                                            @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/auth/api/v1/admin/block")
    ResponseEntity<Void> blockUser(@RequestParam Long id,
                                   @RequestHeader("X-API-Key") String apiKey);
}
