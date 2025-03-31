package ru.mooncess.media_catalog_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mooncess.media_catalog_service.services.ProducerService;
import ru.mooncess.media_catalog_service.services.RevenueService;

@RestController
@RequestMapping("/mcs/api/v1/revenue")
@RequiredArgsConstructor
public class RevenueController {
    private final RevenueService revenueService;
    private final ProducerService producerService;
    @GetMapping("/find-by-producer")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> findAllByProducer(Authentication authentication) {
        try {
            return ResponseEntity.ok(revenueService.findAllByProducer(
                    producerService.findByEmail(authentication.getName()).get().getId()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
