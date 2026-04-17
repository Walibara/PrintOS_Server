package com.capstone.printos_server.storage;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3Service {

    private static final String BUCKET_NAME = "printos-job-storage";
    private static final Region REGION = Region.US_EAST_2;

    public String generateUploadUrl(String key, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            PutObjectRequest.Builder putObjectBuilder = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key);

            if (contentType != null && !contentType.isBlank()) {
                putObjectBuilder.contentType(contentType);
            }

            PutObjectRequest putObjectRequest = putObjectBuilder.build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            return presigner.presignPutObject(presignRequest)
                    .url()
                    .toString();
        }
    }
}
