package com.kaydev.appstore.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
// import java.util.Date;

@Slf4j
@Service
public class AwsService {
    @Value("${aws.bucket}")
    private String aws_s3_bucket;

    @Value("${aws.region}")
    private String aws_region;

    @Autowired
    private AmazonS3 amazonS3;

    public String uploadFile(String fileName, File file) {
        try {
            amazonS3.putObject(new PutObjectRequest(aws_s3_bucket, fileName, file));
            // Date expiration = new Date(System.currentTimeMillis() + 3600000);
            // GeneratePresignedUrlRequest generatePresignedUrlRequest = new
            // GeneratePresignedUrlRequest(aws_s3_bucket,
            // fileName)
            // .withMethod(HttpMethod.GET);
            // URL preSignedUrl =
            // amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            return generateS3ObjectUrl(fileName);

        } catch (Exception e) {
            log.error("S3 uploadFile Exception", e);
            throw new IllegalStateException("Unable to upload file");

        }
    }

    public String generateS3ObjectUrl(String objectKey) {
        String s3BaseUrl = String.format("https://%s.s3.%s.amazonaws.com/", aws_s3_bucket, aws_region);
        return s3BaseUrl + objectKey;
    }

}