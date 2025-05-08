package ru.mooncess.trading_operations_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.trading_operations_service.services.WithdrawService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/tos/api/v1/withdraw")
@RequiredArgsConstructor
public class WithdrawController {
    private final WithdrawService withdrawService;
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> createWithdraw(Authentication authentication,
                                     @RequestParam BigDecimal amount,
                                     @RequestParam String destination) {
        try {
            withdrawService.createWithdraw(authentication.getName(), amount, destination);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find-by-producer")
    @PreAuthorize("hasAuthority('USER')")
    ResponseEntity<?> findAllByProducer(Authentication authentication) {
        try {
            return ResponseEntity.ok(withdrawService.findAllByProducer(authentication.getName()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
