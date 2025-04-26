package com.vady.photoservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

//    @Value("${aws.cloudfront.domain}")
//    private String cloudfrontDomain;

    public String uploadFile(MultipartFile file, String directory) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String key =directory + "/" + UUID.randomUUID() + fileExtension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return CloudFront URL if configured, otherwise direct S3 URL
//            if (cloudfrontDomain != null && !cloudfrontDomain.isEmpty()) {
//                return "https://" + cloudfrontDomain + "/" + key;
//            } else {
//                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            return "/"+key;
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void deleteFile(String key) {
        try {
            // Remove the first character from the key
            String modifiedKey = key.substring(1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(modifiedKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    private String extractKeyFromUrl(String fileUrl) {
//        if (cloudfrontDomain != null && !cloudfrontDomain.isEmpty() && fileUrl.contains(cloudfrontDomain)) {
//            return fileUrl.substring(fileUrl.indexOf(cloudfrontDomain) + cloudfrontDomain.length() + 1);
//        } else
        if (fileUrl.contains(".s3.amazonaws.com")) {
            return fileUrl.substring(fileUrl.indexOf(".s3.amazonaws.com") + ".s3.amazonaws.com".length() + 1);
        } else {
            throw new IllegalArgumentException("Invalid S3 URL format");
        }
    }
}