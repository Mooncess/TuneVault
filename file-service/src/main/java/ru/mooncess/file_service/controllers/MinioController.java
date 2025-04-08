package ru.mooncess.file_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mooncess.file_service.clients.MediaCatalogClient;
import ru.mooncess.file_service.components.MinioComponent;
import ru.mooncess.file_service.domain.JwtInfo;
import ru.mooncess.file_service.domain.MusicFileURI;
import ru.mooncess.file_service.domain.MusicResourceBaseInfo;
import ru.mooncess.file_service.domain.MusicResourceInfo;
import ru.mooncess.file_service.mapper.MusicResourceMapper;
import ru.mooncess.file_service.utils.JwtChecker;

@RestController
@RequestMapping("/s3/api/v1")
@RequiredArgsConstructor
public class MinioController {
    @Autowired
    private MinioComponent minioComponent;

    private final JwtChecker jwtChecker;
    private final MusicResourceMapper mapper;
    private final MediaCatalogClient mediaCatalogClient;

    @Value("${mcs.api.key}")
    private String secretApiKey;
    @Value("${minio.bucket.cover}")
    private String coverBucket;
    @Value("${minio.bucket.demo}")
    private String demoBucket;
    @Value("${minio.bucket.source}")
    private String sourceBucket;
    @Value("${minio.bucket.logo}")
    private String logoBucket;
    @Value("${music.resource.default.cover.uri}")
    private String defaultCoverURI;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMusicResource(@RequestPart @Validated MusicResourceBaseInfo musicResourceBaseInfo,
                                                 @RequestPart(required = false) MultipartFile cover,
                                                 @RequestPart(required = false) MultipartFile demo,
                                                 @RequestPart MultipartFile source,
                                                 HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);

        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MusicResourceInfo musicResourceInfo = mapper.map(musicResourceBaseInfo);

        String coverURI = minioComponent.generateUniqueFileName(cover);
        String demoURI = minioComponent.generateUniqueFileName(demo);
        String sourceURI = minioComponent.generateUniqueFileName(source);

        musicResourceInfo.setCoverURI(coverURI);
        musicResourceInfo.setDemoURI(demoURI);
        musicResourceInfo.setSourceURI(sourceURI);

        try {
            if (mediaCatalogClient.createNewMusicResource(
                    musicResourceInfo, jwtInfo.getUsername(), secretApiKey)
                    .getStatusCode() == HttpStatus.CREATED) {
                minioComponent.putResource(cover, coverURI, coverBucket);
                minioComponent.putResource(demo, demoURI, demoBucket);
                minioComponent.putResource(source, sourceURI, sourceBucket);

                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMusicResource(
            @PathVariable Long id,
            @RequestPart(required = false) MultipartFile cover,
            @RequestPart(required = false) MultipartFile demo,
            @RequestPart(required = false) MultipartFile source,
            HttpServletRequest httpRequest) {

        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            MusicFileURI musicFileURI = mediaCatalogClient
                    .checkOwner(id, jwtInfo.getUsername(), secretApiKey).getBody();

            uploadFilesToStorage(cover, demo, source, musicFileURI);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-cover/{id}")
    public ResponseEntity<?> deleteCover(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            minioComponent.deleteFile(
                    mediaCatalogClient
                            .deleteCover(id, jwtInfo.getUsername(), defaultCoverURI, secretApiKey)
                            .getBody(), coverBucket);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-demo/{id}")
    public ResponseEntity<?> deleteDemo(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            minioComponent.deleteFile(
                    mediaCatalogClient
                            .deleteDemo(id, jwtInfo.getUsername(), secretApiKey)
                            .getBody(), demoBucket);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload/logo")
    public ResponseEntity<?> uploadLogo(@RequestPart MultipartFile logo,
                                        HttpServletRequest httpRequest) {

        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String logoURI = minioComponent.generateUniqueFileName(logo);

        try {
            mediaCatalogClient.uploadLogo(jwtInfo.getUsername(), logoURI, secretApiKey);
            minioComponent.putResource(logo, logoURI, logoBucket);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/logo")
    public ResponseEntity<?> updateLogo(@RequestPart MultipartFile logo,
                                        HttpServletRequest httpRequest) {

        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String logoURI = mediaCatalogClient.updateLogo(jwtInfo.getUsername(), secretApiKey).getBody();
            minioComponent.putResource(logo, logoURI, logoBucket);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/logo")
    public ResponseEntity<?> deleteLogo(HttpServletRequest httpRequest) {
        JwtInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String logoURI = mediaCatalogClient.deleteLogo(jwtInfo.getUsername(), secretApiKey).getBody();
            minioComponent.deleteFile(logoURI, logoBucket);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidUser(JwtInfo jwtInfo) {
        return jwtInfo != null && jwtInfo.getRole() != null && jwtInfo.getRole().equals("USER");
    }

    private MusicFileURI createMusicFileURI(MultipartFile cover, MultipartFile demo, MultipartFile source) {
        MusicFileURI musicFileURI = new MusicFileURI();
        if (cover != null) musicFileURI.setCoverURI(minioComponent.generateUniqueFileName(cover));
        if (demo != null) musicFileURI.setDemoURI(minioComponent.generateUniqueFileName(demo));
        if (source != null) musicFileURI.setSourceURI(minioComponent.generateUniqueFileName(source));
        return musicFileURI;
    }

    private void uploadFilesToStorage(
            MultipartFile cover,
            MultipartFile demo,
            MultipartFile source,
            MusicFileURI musicFileURI) {
        if (cover != null) {
            minioComponent.putResource(cover, musicFileURI.getCoverURI(), coverBucket);
        }
        if (demo != null) {
            minioComponent.putResource(demo, musicFileURI.getDemoURI(), demoBucket);
        }
        if (source != null) {
            minioComponent.putResource(source, musicFileURI.getSourceURI(), sourceBucket);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadSourceFile(@RequestParam String name) throws Exception {
        byte[] data = minioComponent.getObject(name, sourceBucket);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    @GetMapping("/download-source-admin")
    public ResponseEntity<Resource> downloadSourceFileAdmin(@RequestParam Long id,
                                                            @RequestHeader("X-API-Key") String apiKey) throws Exception {
        try {
            return downloadSourceFile(mediaCatalogClient.getSourceURI(id, secretApiKey).getBody());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/admin/music-resource/delete")
    ResponseEntity<Void> deleteMusicResource (@RequestBody MusicFileURI musicFileURI,
                                                      @RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        minioComponent.deleteFile(musicFileURI.getCoverURI(), coverBucket);
        minioComponent.deleteFile(musicFileURI.getDemoURI(), demoBucket);
        minioComponent.deleteFile(musicFileURI.getSourceURI(), sourceBucket);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/media")
    public ResponseEntity<Resource> getMediaFile(@RequestParam String name,
                                                 @RequestParam String type) {
        System.out.println("POINT1");
        try {
            byte[] data;
            String contentType;
            switch (type.toLowerCase()) {
                case "cover" -> {
                    data = minioComponent.getObject(name, coverBucket);
                    contentType = "image/jpeg";
                }
                case "demo" -> {
                    data = minioComponent.getObject(name, demoBucket);
                    contentType = "audio/mpeg";
                }
                case "logo" -> {
                    data = minioComponent.getObject(name, logoBucket);
                    contentType = "image/png";
                }
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }

            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isValidApiKey(String apiKey) {
        return secretApiKey.equals(apiKey);
    }
}
