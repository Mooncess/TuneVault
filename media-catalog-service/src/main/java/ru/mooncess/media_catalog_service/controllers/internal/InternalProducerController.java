package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.services.ProducerService;

@RestController
@RequestMapping("/internal/mcs/api/v1/producer")
@RequiredArgsConstructor
public class InternalProducerController {
    @Value("${mcs.api.key}")
    private String secretApiKey;
    private final ProducerService producerService;

    @PostMapping("/logo")
    ResponseEntity<Void> uploadLogo (@RequestParam String username,
                                       @RequestParam String logoURI,
                                       @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            producerService.uploadLogo(username, logoURI);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logo/get")
    ResponseEntity<String> updateLogo (@RequestParam String username,
                                       @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(producerService.updateLogo(username));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logo/delete")
    ResponseEntity<String> deleteLogo (@RequestParam String username,
                                       @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(producerService.deleteLogo(username));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-producer-id-by-email")
    ResponseEntity<Long> getProducerIdByEmail(@RequestParam String email,
                                              @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return producerService.findByEmail(email)
                .map(i -> ResponseEntity.ok(i.getId()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
