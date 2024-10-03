package com.team9.sungdaehanmarket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final String bucketName = "swe9-image"; // 버킷 이름

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // 파일을 S3에 업로드하고 URL 반환
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 파일 이름 고유하게 생성

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // S3에 업로드된 파일의 경로 또는 URL 반환
        return "https://swe9-image.s3.ap-northeast-2.amazonaws.com/" + fileName;
    }

}