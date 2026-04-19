package com.capstone.printos_server.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.IOException;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String fileType
    ) {
        // Generate a unique S3 key
        String s3Key = "jobs/" + UUID.randomUUID() + "/" + fileName;

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(fileType)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
        );

        presigner.close();

        return ResponseEntity.ok(Map.of(
                "uploadUrl", presignedRequest.url().toString(),
                "s3Key", s3Key
        ));
    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> streamFile(jakarta.servlet.http.HttpServletRequest request) throws IOException {
        // Extract the full s3Key from the path after /api/s3/file/
        String s3Key = request.getRequestURI().split("/api/s3/file/", 2)[1];

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        byte[] bytes = s3Object.readAllBytes();
        String contentType = s3Object.response().contentType();

        s3Client.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                .body(bytes);
    }
}
