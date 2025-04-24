package ru.mooncess.trading_operations_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mooncess.trading_operations_service.services.RevenueService;

@RestController
@RequestMapping("/tos/api/v1/revenue")
@RequiredArgsConstructor
public class RevenueController {
    private final RevenueService revenueService;
    @GetMapping("/find-by-producer")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> findAllByProducer(Authentication authentication) {
        try {
            return ResponseEntity.ok(revenueService.findAllByProducer(authentication.getName()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
