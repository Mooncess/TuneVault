package ru.mooncess.trading_operations_service.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.trading_operations_service.services.ProducerBalanceService;

@RestController
@RequestMapping("/internal/tos/api/v1/producer-balance")
@RequiredArgsConstructor
public class InternalProducerBalanceController {
    @Value("${mcs.api.key}")
    private String secretApiKey;

    private final ProducerBalanceService producerBalanceService;

    @PostMapping("/create")
    ResponseEntity<Long> createNewProducerBalance(@RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(producerBalanceService.createNewProducerBalance());
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
