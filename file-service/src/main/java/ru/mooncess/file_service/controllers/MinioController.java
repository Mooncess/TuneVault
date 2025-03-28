package ru.mooncess.file_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mooncess.file_service.clients.MediaCatalogClient;
import ru.mooncess.file_service.components.MinioComponent;
import ru.mooncess.file_service.domain.JtwInfo;
import ru.mooncess.file_service.domain.MusicFileURI;
import ru.mooncess.file_service.domain.MusicResourceBaseInfo;
import ru.mooncess.file_service.domain.MusicResourceInfo;
import ru.mooncess.file_service.mapper.MusicResourceMapper;
import ru.mooncess.file_service.utils.JwtChecker;

import java.util.concurrent.CompletableFuture;

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
    @Value("${minio.bucket.logo}")
    private String logoBucket;
    @Value("${minio.bucket.demo}")
    private String demoBucket;
    @Value("${minio.bucket.source}")
    private String sourceBucket;
    @Value("${music.resource.default.logo.uri}")
    private String defaultLogoURI;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMusicResource(@RequestPart @Validated MusicResourceBaseInfo musicResourceBaseInfo,
                                                 @RequestPart(required = false) MultipartFile logo,
                                                 @RequestPart(required = false) MultipartFile demo,
                                                 @RequestPart MultipartFile source,
                                                 HttpServletRequest httpRequest) {
        JtwInfo jwtInfo = jwtChecker.checkToken(httpRequest);

        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MusicResourceInfo musicResourceInfo = mapper.map(musicResourceBaseInfo);

        String logoURI = minioComponent.generateUniqueFileName(logo);
        String demoURI = minioComponent.generateUniqueFileName(demo);
        String sourceURI = minioComponent.generateUniqueFileName(source);

        musicResourceInfo.setLogoURI(logoURI);
        musicResourceInfo.setDemoURI(demoURI);
        musicResourceInfo.setSourceURI(sourceURI);

        try {
            if (mediaCatalogClient.createNewMusicResource(
                    musicResourceInfo, jwtInfo.getUsername(), secretApiKey)
                    .getStatusCode() == HttpStatus.CREATED) {
                minioComponent.putResource(logo, logoURI, logoBucket);
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
            @RequestPart(required = false) MultipartFile logo,
            @RequestPart(required = false) MultipartFile demo,
            @RequestPart(required = false) MultipartFile source,
            HttpServletRequest httpRequest) {

        JtwInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            mediaCatalogClient.checkOwner(
                    id, jwtInfo.getUsername(), secretApiKey
            );

            MusicFileURI musicFileURI = createMusicFileURI(logo, demo, source);
            ResponseEntity<?> updateResponse = mediaCatalogClient.updateFilesOfMusicResource(
                    id, musicFileURI, secretApiKey
            );

            if (updateResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            uploadFilesToStorage(logo, demo, source, musicFileURI);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-logo/{id}")
    public ResponseEntity<?> deleteLogo(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        JtwInfo jwtInfo = jwtChecker.checkToken(httpRequest);
        if (!isValidUser(jwtInfo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            minioComponent.deleteFile(
                    mediaCatalogClient
                            .deleteLogo(id, jwtInfo.getUsername(), defaultLogoURI, secretApiKey)
                            .getBody(), logoBucket);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-demo/{id}")
    public ResponseEntity<?> deleteDemo(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        JtwInfo jwtInfo = jwtChecker.checkToken(httpRequest);
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

    private boolean isValidUser(JtwInfo jwtInfo) {
        return jwtInfo != null && jwtInfo.getRole() != null && jwtInfo.getRole().equals("USER");
    }

    private MusicFileURI createMusicFileURI(MultipartFile logo, MultipartFile demo, MultipartFile source) {
        MusicFileURI musicFileURI = new MusicFileURI();
        if (logo != null) musicFileURI.setLogoURI(minioComponent.generateUniqueFileName(logo));
        if (demo != null) musicFileURI.setDemoURI(minioComponent.generateUniqueFileName(demo));
        if (source != null) musicFileURI.setSourceURI(minioComponent.generateUniqueFileName(source));
        return musicFileURI;
    }

    private void uploadFilesToStorage(
            MultipartFile logo,
            MultipartFile demo,
            MultipartFile source,
            MusicFileURI musicFileURI) {
        if (logo != null) {
            minioComponent.putResource(logo, musicFileURI.getLogoURI(), logoBucket);
        }
        if (demo != null) {
            minioComponent.putResource(demo, musicFileURI.getDemoURI(), demoBucket);
        }
        if (source != null) {
            minioComponent.putResource(source, musicFileURI.getSourceURI(), sourceBucket);
        }
    }

//    @PostMapping("/update/{id}")
//    public ResponseEntity<?> updateMusicResource(@RequestPart @Validated MusicResourceBaseInfo musicResourceBaseInfo,
//                                                 @RequestPart(required = false) MultipartFile logo,
//                                                 @RequestPart(required = false) MultipartFile demo,
//                                                 @RequestPart MultipartFile source,
//                                                 HttpServletRequest httpRequest) {
//        JtwInfo jwtInfo = jwtChecker.checkToken(httpRequest);
//
//        if (jwtInfo == null || jwtInfo.getRole() == null || !jwtInfo.getRole().equals("USER")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        MusicResourceInfo musicResourceInfo = mapper.map(musicResourceBaseInfo);
//
//        String logoURI = minioComponent.generateUniqueFileName(logo, musicResourceBaseInfo.getName());
//        String demoURI = minioComponent.generateUniqueFileName(demo, musicResourceBaseInfo.getName());
//        String sourceURI = minioComponent.generateUniqueFileName(source, musicResourceBaseInfo.getName());
//
//        musicResourceInfo.setLogoURI(logoURI);
//        musicResourceInfo.setDemoURI(demoURI);
//        musicResourceInfo.setSourceURI(sourceURI);
//
//        if (mediaCatalogClient.createNewMusicResource(musicResourceInfo,jwtInfo.getUsername(), secretApiKey).getStatusCode() == HttpStatus.CREATED) {
//            minioComponent.putResource(logo, logoURI, logoBucket);
//            minioComponent.putResource(demo, demoURI, demoBucket);
//            minioComponent.putResource(source, sourceURI, sourceBucket);
//
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }

//    @PostMapping("/upload")
//    public String uploadFileToMinIO(@RequestParam("file") MultipartFile file) {
//        try {
//            InputStream in = new ByteArrayInputStream(file.getBytes());
//            String fileName = file.getOriginalFilename();
//            minioComponent.putObject(fileName, in);
//            return "File uploaded.";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "Something wrong.";
//    }

//    @PostMapping("/upload")
//    public String uploadFileToMinIO(@RequestParam("file") MultipartFile file) {
//        try {
//            InputStream in = new ByteArrayInputStream(file.getBytes());
//            String fileName = file.getOriginalFilename();
//            minioComponent.putObject(fileName, in);
//            return "File uploaded.";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "Something wrong.";
//    }

//    @GetMapping("/download")
//    public String downloadFile(@RequestParam String name) throws Exception {
//        return minioComponent.getObject(name);
//    }

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

    @Async
    public CompletableFuture<byte[]> downloadFileAsync(String name, String bucketName) {
        return CompletableFuture.completedFuture(minioComponent.getObject(name, bucketName));
    }

//    @GetMapping("/download")
//    public String downloadSourceFile(@RequestParam String name) throws Exception {
//        return minioComponent.getObject(name, sourceBucket);
//    }

//    @GetMapping("/download")
//    public CompletableFuture<ResponseEntity<Resource>> downloadFile(@RequestParam String name) {
//        return downloadFileAsync(name).thenApply(data -> {
//            ByteArrayResource resource = new ByteArrayResource(data);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .contentLength(data.length)
//                    .body(resource);
//        });
//    }



    @GetMapping("/hello")
    public ResponseEntity<?> helloTest() {
        return ResponseEntity.ok().body("HELLO!");
    }

//    @GetMapping("/download-song")
//    public ResponseEntity<Resource> downloadSong(@RequestParam String name) throws Exception {
//        // Получаем объект из MinIO
//        byte[] data = minioComponent.getObject(name).getBytes();
//
//        // Проверяем, что данные не пустые
//        if (data.length == 0) {
//            return ResponseEntity.notFound().build(); // Вернуть 404, если файл не найден
//        }
//
//        ByteArrayResource resource = new ByteArrayResource(data);
//
//        // Устанавливаем заголовки для скачивания
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
//                .contentType(MediaType.) // Устанавливаем тип для MP3
//                .contentLength(data.length)
//                .body(resource);
//    }

//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam String name) throws Exception {
//        // Получаем объект из MinIO
//        byte[] data = minioComponent.getObject(name).getBytes();
//
//        // Проверяем, что данные не пустые
//        if (data.length == 0) {
//            return ResponseEntity.notFound().build(); // Вернуть 404, если файл не найден
//        }
//
//        ByteArrayResource resource = new ByteArrayResource(data);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
//
//        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//    }

//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam String name) {
//        try {
//            // Получаем объект из MinIO
//            byte[] data = minioComponent.getObject(name).getBytes();
//
//            // Проверяем, что данные не пустые
//            if (data.length == 0) {
//                return ResponseEntity.notFound().build(); // Вернуть 404, если файл не найден
//            }
//
//            ByteArrayResource resource = new ByteArrayResource(data);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"");
//
//            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Вернуть 500 в случае ошибки
//        }
//    }
}
