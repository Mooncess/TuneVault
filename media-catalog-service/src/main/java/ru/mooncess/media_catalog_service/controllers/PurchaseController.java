package ru.mooncess.media_catalog_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.media_catalog_service.services.PurchaseService;


@RestController
@RequestMapping("/mcs/api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/{id}")
    ResponseEntity<?> purchase(@PathVariable Long id,
                               @RequestParam @Validated String email) {
        try {
            return ResponseEntity.ok(purchaseService.createSale(id, email));
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/confirm/{id}")
    ResponseEntity<?> confirm(@PathVariable Long id) {
        purchaseService.confirm(id);
        return ResponseEntity.ok().build();
    }
}
