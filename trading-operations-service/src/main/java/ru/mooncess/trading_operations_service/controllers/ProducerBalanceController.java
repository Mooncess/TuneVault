package ru.mooncess.trading_operations_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mooncess.trading_operations_service.services.ProducerBalanceService;

@RestController
@RequestMapping("/tos/api/v1/producer-balance")
@RequiredArgsConstructor
public class ProducerBalanceController {
    private final ProducerBalanceService producerBalanceService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> getBalance(Authentication authentication) {
        try {
            return ResponseEntity.ok(producerBalanceService.getBalanceByEmail(authentication.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
