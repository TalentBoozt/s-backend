package com.talentboozt.s_backend.domains.edu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
public class R2StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrl;

    public R2StorageService(
            @Value("${r2.endpoint:https://id.r2.cloudflarestorage.com}") String endpoint,
            @Value("${r2.access-key:world}") String accessKey,
            @Value("${r2.secret-key:world}") String secretKey,
            @Value("${r2.bucket-name:talnova-media}") String bucketName,
            @Value("${r2.public-url:https://pub-id.r2.dev}") String publicUrl
    ) {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .region(Region.US_EAST_1) // SDK requires a region even if R2 is regional
                .build();
        this.bucketName = bucketName;
        this.publicUrl = publicUrl;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("Successfully uploaded file {} to R2", fileName);
            
            // Handle trailing slash in publicUrl
            String baseUrl = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
            return baseUrl + "/" + fileName;
        } catch (Exception e) {
            log.error("Failed to upload file to R2: {}", e.getMessage());
            throw new IOException("Could not upload file to storage", e);
        }
    }
}
