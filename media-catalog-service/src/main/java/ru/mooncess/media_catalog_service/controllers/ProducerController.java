package ru.mooncess.media_catalog_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.entities.Producer;
import ru.mooncess.media_catalog_service.services.ProducerService;

import java.util.List;

@RestController
@RequestMapping("/mcs/api/v1/producer")
@RequiredArgsConstructor
public class ProducerController {
    private final ProducerService producerService;

    @PutMapping("/update-info")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> updateProducer(@RequestBody @Validated UpdateProducerInfo updateProducerInfo,
                                     Authentication authentication) {
        if (producerService.updateProducer(updateProducerInfo, authentication.getName())) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/")
    ResponseEntity<List<Producer>> findAllUserProducers(@RequestParam(required = false) String nickname) {
        if (nickname == null) return ResponseEntity.ok(producerService.findAllUserProducers());
        else return ResponseEntity.ok(producerService.findProducersByNicknamePart(nickname));
    }

    @GetMapping("/{id}")
    ResponseEntity<Producer> findProducerById(@PathVariable Long id) {
        return producerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/email")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<String> getUserEmail(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    ResponseEntity<Producer> profile(Authentication authentication) {
        return producerService.findByEmail(authentication.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
