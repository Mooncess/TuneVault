package ru.mooncess.media_catalog_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.services.ProducerService;

@RestController
@RequestMapping("/mcs/producer/api/v1")
@RequiredArgsConstructor
public class ProducerController {
    private final ProducerService producerService;

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> helloString(Authentication authentication) {
        return ResponseEntity.ok("Hello!");
    }

    @PostMapping("/update-info/{id}")
    @PreAuthorize("hasAuthority('USER') and #authentication.name == @producerRepository.findByEmail(#updateProducerInfo.email).orElseThrow().email")
    ResponseEntity<?> updateProducer(@RequestBody @Validated UpdateProducerInfo updateProducerInfo,
                                     Authentication authentication) {
        if (producerService.updateProducer(updateProducerInfo)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }
}
