package com.capstone.printos_server.storage;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload-url")
    public Map<String, String> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam(required = false) String contentType
    ) {
        String userId = "test-user";
        String key = "users/" + userId + "/uploads/" + fileName;

        String uploadUrl = s3Service.generateUploadUrl(key, contentType);

        return Map.of(
                "uploadUrl", uploadUrl,
                "key", key
        );
    }
}