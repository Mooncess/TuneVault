package ru.mooncess.admin_panel_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.admin_panel_service.domain.JwtInfo;
import ru.mooncess.admin_panel_service.dto.CreateClaimDto;
import ru.mooncess.admin_panel_service.entity.Claim;
import ru.mooncess.admin_panel_service.service.ClaimService;
import ru.mooncess.admin_panel_service.utils.JwtChecker;

import java.util.List;

@RestController
@RequestMapping("/aps/api/v1/claim")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;
    private final JwtChecker jwtChecker;

    @PostMapping("/create")
    ResponseEntity<?> createClaim(@RequestBody @Validated CreateClaimDto createClaimDto) {
        try {
            claimService.createClaim(createClaimDto);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/")
    ResponseEntity<List<Claim>> findAllClaimByStatus(@RequestParam(required = false, defaultValue = "0") int status,
                                                     @RequestParam(required = false, defaultValue = "0") int sort,
                                                     HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);

        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(claimService.findAllClaimsByStatus(status, sort));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> findAllClaimByStatus(@PathVariable Long id,
                                                     HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);

        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return claimService.findAllClaimById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/music-resource/{claimId}")
    ResponseEntity<?> getMusicResourceFromClaim(@PathVariable Long claimId,
                                                HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(claimService.getMusicResourceFromClaim(claimId));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{claimId}/accept")
    ResponseEntity<?> acceptClaim(@PathVariable Long claimId,
                                  HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            claimService.setClaimAcceptStatus(claimId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{claimId}/block")
    ResponseEntity<?> blockProducerFromClaim(@PathVariable Long claimId,
                                             HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            claimService.setClaimBlockStatus(claimId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{claimId}/review")
    ResponseEntity<?> reviewClaim(@PathVariable Long claimId,
                                  HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (jwtInfo == null || !isValidAdmin(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            claimService.setClaimReviewStatus(claimId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidAdmin(JwtInfo jwtInfo) {
        return jwtInfo.getRole() != null && jwtInfo.getRole().equals("ADMIN");
    }
}
