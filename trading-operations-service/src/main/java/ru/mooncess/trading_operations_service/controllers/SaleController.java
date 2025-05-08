package ru.mooncess.trading_operations_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.trading_operations_service.services.SaleService;

@RestController
@RequestMapping("/tos/api/v1/sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping("/{id}")
    ResponseEntity<?> purchase(@PathVariable Long id,
                               @RequestParam @Validated String email) {
        try {
            return ResponseEntity.ok(saleService.createSale(id, email));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/confirm/{id}")
    ResponseEntity<?> confirm(@PathVariable Long id) {
        saleService.confirm(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-by-music-resource/{id}")
    ResponseEntity<?> findAllByMusicResource(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(saleService.findAllByMusicResource(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
