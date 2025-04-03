package ru.mooncess.admin_panel_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.mooncess.admin_panel_service.dto.MusicFileURI;

@FeignClient(name = "FILE-SERVICE")
public interface FileServiceClient {
    @DeleteMapping("/s3/api/v1/admin/music-resource/delete")
    ResponseEntity<MusicFileURI> deleteMusicResource (@RequestBody MusicFileURI musicFileURI,
                                                      @RequestHeader("X-API-Key") String apiKey);
}
