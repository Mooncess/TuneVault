package ru.mooncess.media_catalog_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.dto.ProducerInfo;
import ru.mooncess.media_catalog_service.services.ProducerService;

@RestController
@RequestMapping("/internal/mcs/api/v1/producer")
@RequiredArgsConstructor
public class InternalAuthController {
    private final ProducerService producerService;
    @Value("${mcs.api.key}")
    private String secretApiKey;

    @PostMapping("/registration")
    ResponseEntity<Long> registrationNewProducer(@RequestBody @Validated ProducerInfo producerInfo,
                                                 @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (producerService.createNewProducer(producerInfo)) {
            long id = producerService.findByEmail(producerInfo.getEmail()).orElseThrow().getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/updateEmail")
    ResponseEntity<Void> updateEmailOfProducer(@RequestParam String newEmail,
                                               @RequestParam String oldEmail,
                                               @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        producerService.updateEmailOfProducer(newEmail, oldEmail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteProducer(@RequestParam Long id,
                                        @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        producerService.deleteProducer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/disable")
    ResponseEntity<Void> updateEmailOfProducer(@RequestParam Long id,
                                               @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            producerService.disable(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
