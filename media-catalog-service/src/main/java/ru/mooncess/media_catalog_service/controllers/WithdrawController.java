package ru.mooncess.media_catalog_service.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.services.ProducerService;
import ru.mooncess.media_catalog_service.services.RevenueService;
import ru.mooncess.media_catalog_service.services.WithdrawService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/mcs/api/v1/withdraw")
@RequiredArgsConstructor
public class WithdrawController {
    private final ProducerService producerService;
    private final WithdrawService withdrawService;
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> createWithdraw(Authentication authentication,
                                     @RequestParam BigDecimal amount,
                                     @RequestParam String destination) {
        try {
            withdrawService.createWithdraw(
                    producerService.findByEmail(authentication.getName()).get(), amount, destination);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
