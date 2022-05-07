package com.densoft.shopmeAdmin;

import com.densoft.shopmeAdmin.util.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AmazonS3Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Util.class);

    public static final String BUCKET_NAME;

    static {
        BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    }


    public static List<String> listFolder(String folderName) {
        S3Client s3Client = S3Client.builder().build();
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();
        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        List<S3Object> s3ObjectList = listObjectsResponse.contents();

        List<String> listKeys = new ArrayList<>();

        s3ObjectList.forEach(s3Object -> {
            listKeys.add(s3Object.key());
        });

        return listKeys;

    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
        S3Client s3Client = S3Client.builder().build();
        PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET_NAME)
                .key(folderName + "/" + fileName).acl("public-read").build();
        try (inputStream) {
            int contentLength = inputStream.available();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (IOException e) {
            LOGGER.error("Could not upload file to amazon s3", e);
        }
    }

    public static void deleteFile(String fileName) {
        S3Client s3Client = S3Client.builder().build();
        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
                .key(fileName).build();

        s3Client.deleteObject(request);
    }

    public static void removeFolder(String folderName) {

        S3Client s3Client = S3Client.builder().build();
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();
        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        List<S3Object> s3ObjectList = listObjectsResponse.contents();

        s3ObjectList.forEach(s3Object -> {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
                    .key(s3Object.key()).build();
            s3Client.deleteObject(request);
            System.out.println("Deleted " + s3Object.key());
        });


    }
}
