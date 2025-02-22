package com.project.userservice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.project.userservice.constants.AwsS3Properties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AwsS3Properties properties;
    private final AmazonS3 client;

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
