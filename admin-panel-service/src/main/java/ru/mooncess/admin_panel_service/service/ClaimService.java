package ru.mooncess.admin_panel_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mooncess.admin_panel_service.client.FileServiceClient;
import ru.mooncess.admin_panel_service.client.MediaCatalogClient;
import ru.mooncess.admin_panel_service.dto.CreateClaimDto;
import ru.mooncess.admin_panel_service.dto.MusicFileURI;
import ru.mooncess.admin_panel_service.entity.Claim;
import ru.mooncess.admin_panel_service.domain.enums.ClaimStatus;
import ru.mooncess.admin_panel_service.repository.ClaimRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {
    @Value("${secret.api.key}")
    private String secretApiKey;
    private final ClaimRepository claimRepository;
    private final MediaCatalogClient mediaCatalogClient;
    private final FileServiceClient fileServiceClient;
    public void createClaim(CreateClaimDto createClaimDto) {
        Claim claim = new Claim();

        claim.setSenderEmail(createClaimDto.getSenderEmail());
        claim.setDescription(createClaimDto.getDescription());
        claim.setMusicResourceId(createClaimDto.getMusicResourceId());
        claim.setStatus(ClaimStatus.NOT_PROCESSED);
        claim.setCreatedDate(LocalDateTime.now());

        claimRepository.save(claim);
    }

    public List<Claim> findAllClaimsByStatus(int filter, int sort) {
        switch (filter) {
            case 0 -> {
                if (sort == 0) return claimRepository.findAllByStatus(ClaimStatus.NOT_PROCESSED, Sort.by("createdDate").ascending());
                else return claimRepository.findAllByStatus(ClaimStatus.NOT_PROCESSED, Sort.by("createdDate").descending());
            }
            case 1 -> {
                if (sort == 0) return claimRepository.findAllByStatus(ClaimStatus.REVIEWED, Sort.by("createdDate").ascending());
                else return claimRepository.findAllByStatus(ClaimStatus.REVIEWED, Sort.by("createdDate").descending());
            }
            case 2 -> {
                if (sort == 0) return claimRepository.findAllByStatus(ClaimStatus.ACCEPTED, Sort.by("createdDate").ascending());
                else return claimRepository.findAllByStatus(ClaimStatus.ACCEPTED, Sort.by("createdDate").descending());
            }
            default -> {
                return null;
            }
        }
    }

    public String getMusicResourceFromClaim(Long id) {
        var claim = claimRepository.findById(id);
        if (claim.isPresent()) {
            try {
                return mediaCatalogClient.getMusicResourceSource(claim.get().getMusicResourceId(), secretApiKey).getBody();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Couldn't send request to media-catalog-service");
            }

        }
        else throw new RuntimeException("Claim not found with id: " + id);
    }

    public void setClaimReviewStatus(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));

        claim.setStatus(ClaimStatus.REVIEWED);
        claimRepository.save(claim);
    }

    public void setClaimAcceptStatus(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));

        try {
            ResponseEntity<MusicFileURI> response = mediaCatalogClient.strikeAndDelete(claim.getMusicResourceId(), secretApiKey);
            if (response.getStatusCode() == HttpStatus.OK) fileServiceClient.deleteMusicResource(response.getBody(), secretApiKey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Couldn't send request to other services");
        }

        claim.setStatus(ClaimStatus.ACCEPTED);
        claimRepository.save(claim);
    }
}
