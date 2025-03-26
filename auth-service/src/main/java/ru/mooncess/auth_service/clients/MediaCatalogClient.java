package ru.mooncess.auth_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.auth_service.domain.ProducerInfo;

@FeignClient(name = "MEDIA-CATALOG-SERVICE")
public interface MediaCatalogClient {
    @PostMapping("/internal/mcs/api/v1/producer/registration")
    ResponseEntity<Long> registrationNewProducer(@RequestBody ProducerInfo producerInfo,
                                                 @RequestHeader("X-API-Key") String apiKey);

    @PutMapping("/internal/mcs/api/v1/producer/updateEmail")
    ResponseEntity<Void> updateEmailOfProducer(@RequestParam String newEmail,
                                               @RequestParam String oldEmail,
                                               @RequestHeader("X-API-Key") String apiKey);

    @DeleteMapping("/internal/mcs/api/v1/producer/delete")
    ResponseEntity<Void> deleteProducer(@RequestParam Long id,
                                               @RequestHeader("X-API-Key") String apiKey);

}
