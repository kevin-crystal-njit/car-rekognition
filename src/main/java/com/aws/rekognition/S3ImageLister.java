package com.aws.rekognition;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.util.List;

public class S3ImageLister {
    private static final String BUCKET_NAME = "njit-cs-643";

    public static void main(String[] args) {
        // Create an S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        System.out.println("Listing images from S3 bucket: " + BUCKET_NAME);

        // Create a request to list objects in the bucket
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .maxKeys(10)
                .build();

        // Get the response
        ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);
        List<S3Object> objects = response.contents();

        // Print the file names
        for (S3Object object : objects) {
            System.out.println("Found file: " + object.key());
        }

        s3Client.close();
    }
}

