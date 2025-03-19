package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.dto.ProducerInfo;
import ru.mooncess.media_catalog_service.dto.UpdateProducerInfo;
import ru.mooncess.media_catalog_service.services.ProducerService;

@RestController
@RequestMapping("/internal/mcs/producer/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final ProducerService producerService;
    @Value("${mcs.api.key}")
    private String secretApiKey;

    @PostMapping("/registration")
    ResponseEntity<Void> registrationNewProducer(@RequestBody @Validated ProducerInfo producerInfo,
                                                 @RequestHeader("X-API-Key") String apiKey) {
        System.out.println("REG REQ : " + apiKey);

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        producerService.createNewProducer(producerInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/updateEmail")
    ResponseEntity<Void> updateEmailOfProducer(@RequestParam String newEmail,
                                               @RequestParam String oldEmail,
                                               @RequestHeader("X-API-Key") String apiKey) {
        System.out.println("UPDATE REQ : " + apiKey);

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        producerService.updateEmailOfProducer(newEmail, oldEmail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteProducer(@RequestParam Long id,
                                        @RequestHeader("X-API-Key") String apiKey) {
        System.out.println("DELETE REQ : " + apiKey);

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        producerService.deleteProducer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
