package com.project.catalog_service.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import com.project.catalog_service.constants.AwsS3Properties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AwsS3Properties properties;
    private final AmazonS3 client;

    public String upload(String path) throws IOException {

        // ClassPathResource를 사용하여 resources 폴더 내의 파일을 가져옵니다.
        ClassPathResource resource = new ClassPathResource(path);

        // 파일이 존재하지 않는 경우 예외 처리
        if (!resource.exists()) {
            throw new IllegalArgumentException("File not found in classpath: " + path);
        }

        String bucket = properties.getBucket();

        // S3에 업로드할 파일의 InputStream을 얻습니다.
        InputStream inputStream = resource.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        ByteArrayInputStream stream = getByteArrayInputStream(inputStream, metadata);

        client.putObject(new PutObjectRequest(bucket, resource.getFilename(), stream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return client.getUrl(bucket, resource.getFilename()).toString();

    }

    private ByteArrayInputStream getByteArrayInputStream(InputStream inputStream, ObjectMetadata metadata)
            throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        metadata.setContentLength(bytes.length);
        return new ByteArrayInputStream(bytes);
    }

    public String upload(MultipartFile file, String filename) throws IOException {

        return putObjectToS3Storage(client, filename, file);
    }

    private String putObjectToS3Storage(AmazonS3 client, String filepath, MultipartFile file) throws IOException {
        String bucket = properties.getBucket();

        ObjectMetadata metadata = new ObjectMetadata();
        ByteArrayInputStream stream = getByteArrayInputStream(file, metadata);

        client.putObject(new PutObjectRequest(bucket, filepath, stream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return client.getUrl(bucket, filepath).toString();
    }

    private ByteArrayInputStream getByteArrayInputStream(MultipartFile file, ObjectMetadata metadata)
            throws IOException {
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        metadata.setContentLength(bytes.length);
        return new ByteArrayInputStream(bytes);
    }

}
