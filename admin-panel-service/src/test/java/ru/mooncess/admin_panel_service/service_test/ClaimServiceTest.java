package ru.mooncess.admin_panel_service.service_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mooncess.admin_panel_service.client.FileServiceClient;
import ru.mooncess.admin_panel_service.client.MediaCatalogClient;
import ru.mooncess.admin_panel_service.domain.enums.ClaimStatus;
import ru.mooncess.admin_panel_service.dto.CreateClaimDto;
import ru.mooncess.admin_panel_service.dto.MusicFileURI;
import ru.mooncess.admin_panel_service.entity.Claim;
import ru.mooncess.admin_panel_service.repository.ClaimRepository;
import ru.mooncess.admin_panel_service.service.ClaimService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @InjectMocks
    private ClaimService claimService;

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private MediaCatalogClient mediaCatalogClient;

    @Mock
    private FileServiceClient fileServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(claimService, "secretApiKey", "test-secret");
    }

    @Test
    void createClaim_shouldSaveClaim() {
        CreateClaimDto dto = new CreateClaimDto();
        dto.setSenderEmail("user@mail.com");
        dto.setMusicResourceId(1L);
        dto.setDescription("bad music");

        claimService.createClaim(dto);

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(captor.capture());

        Claim saved = captor.getValue();
        assertEquals("user@mail.com", saved.getSenderEmail());
        assertEquals("bad music", saved.getDescription());
        assertEquals(1L, saved.getMusicResourceId());
        assertEquals(ClaimStatus.NOT_PROCESSED, saved.getStatus());
        assertNotNull(saved.getCreatedDate());
    }

    @Test
    void findAllClaimsByStatus_shouldReturnSortedClaims() {
        List<Claim> dummyList = List.of(new Claim());
        when(claimRepository.findAllByStatus(eq(ClaimStatus.NOT_PROCESSED), any(Sort.class)))
                .thenReturn(dummyList);

        List<Claim> result = claimService.findAllClaimsByStatus(0, 0);

        assertEquals(1, result.size());
        verify(claimRepository).findAllByStatus(eq(ClaimStatus.NOT_PROCESSED), any(Sort.class));
    }

    @Test
    void getMusicResourceFromClaim_shouldReturnUri() {
        Claim claim = new Claim();
        claim.setMusicResourceId(42L);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(mediaCatalogClient.getMusicResourceSource(eq(42L), any()))
                .thenReturn(ResponseEntity.ok("http://resource"));

        String result = claimService.getMusicResourceFromClaim(1L);

        assertEquals("http://resource", result);
    }

    @Test
    void getMusicResourceFromClaim_shouldThrowIfClaimNotFound() {
        when(claimRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> claimService.getMusicResourceFromClaim(1L));

        assertEquals("Claim not found with id: 1", ex.getMessage());
    }

    @Test
    void setClaimReviewStatus_shouldSetReviewed() {
        Claim claim = new Claim();
        claim.setStatus(ClaimStatus.NOT_PROCESSED);
        when(claimRepository.findById(5L)).thenReturn(Optional.of(claim));

        claimService.setClaimReviewStatus(5L);

        assertEquals(ClaimStatus.REVIEWED, claim.getStatus());
        verify(claimRepository).save(claim);
    }

    @Test
    void setClaimAcceptStatus_shouldStrikeAndDeleteAndAccept() {
        Claim claim = new Claim();
        claim.setMusicResourceId(101L);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        MusicFileURI uri = new MusicFileURI();
        uri.setSourceURI("some-uri");
        when(mediaCatalogClient.strikeAndDelete(101L, "test-secret"))
                .thenReturn(ResponseEntity.ok(uri));

        claimService.setClaimAcceptStatus(1L);

        verify(fileServiceClient).deleteMusicResource(uri, "test-secret");
        assertEquals(ClaimStatus.ACCEPTED, claim.getStatus());
        verify(claimRepository).save(claim);
    }

    @Test
    void setClaimAcceptStatus_shouldThrowOnMediaCatalogFailure() {
        Claim claim = new Claim();
        claim.setMusicResourceId(101L);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(mediaCatalogClient.strikeAndDelete(101L, "test-secret"))
                .thenThrow(new RuntimeException("fail"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> claimService.setClaimAcceptStatus(1L));

        assertTrue(ex.getMessage().contains("Couldn't send request to other services"));
    }

    @Test
    void findAllClaimById_shouldReturnOptional() {
        Claim claim = new Claim();
        when(claimRepository.findById(99L)).thenReturn(Optional.of(claim));

        Optional<Claim> result = claimService.findAllClaimById(99L);

        assertTrue(result.isPresent());
    }
}

