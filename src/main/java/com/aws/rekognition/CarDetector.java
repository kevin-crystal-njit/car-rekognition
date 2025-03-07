package com.aws.rekognition;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class CarDetector {
    private static final String BUCKET_NAME = "njit-cs-643";

    public static void main(String[] args) {
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        System.out.println("Analyzing all images in S3 bucket: " + BUCKET_NAME);

        // List all objects in the bucket
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        List<S3Object> s3Objects = listResponse.contents();

        for (S3Object object : s3Objects) {
            String fileName = object.key();

            System.out.println("Analyzing file: " + fileName);

            // Build the Rekognition S3 object
            software.amazon.awssdk.services.rekognition.model.S3Object rekognitionS3Object = 
                software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                    .bucket(BUCKET_NAME)
                    .name(fileName)
                    .build();

            // Configure the image
            Image image = Image.builder()
                    .s3Object(rekognitionS3Object)
                    .build();

            // Set up the Rekognition request
            DetectLabelsRequest detectRequest = DetectLabelsRequest.builder()
                    .image(image)
                    .maxLabels(10)
                    .minConfidence(75F)
                    .build();

            DetectLabelsResponse response = rekognitionClient.detectLabels(detectRequest);

            // Check for "Car" labels
            boolean containsCar = response.labels().stream()
                    .anyMatch(label -> label.name().equalsIgnoreCase("Car"));

            System.out.println("File: " + fileName + " - Contains car: " + containsCar);
        }

        rekognitionClient.close();
        s3Client.close();
    }
}

