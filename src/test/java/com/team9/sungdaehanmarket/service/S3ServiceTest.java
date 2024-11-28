package com.team9.sungdaehanmarket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    @Transactional
    @DisplayName(value = "S3 파일 업로드 test")
    public void getBucketNameTest() throws IOException {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "홍길동전 썸네일 이미지",
                "thumbnail.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );
        //when
        String bucketName = s3Service.uploadFile(file);
        //then
        assertThat(bucketName).contains(file.getOriginalFilename());
    }
}
