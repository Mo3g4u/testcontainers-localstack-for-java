package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class HandlerTest {
    @Container
    private static final LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.8"))
            .withServices(LocalStackContainer.Service.S3);

    private static S3Client s3Client;

    @BeforeAll
    public static void setUpAll() throws Exception {
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .endpointOverride(URI.create(localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString()))
                .forcePathStyle(true)
                .build();
    }

    @Test
    void createBucket() {
        Handler.createBucket(s3Client, "test-bucket");
        // Check if the bucket exists
        assertTrue(s3Client.listBuckets().buckets().stream().anyMatch(bucket -> bucket.name().equals("test-bucket")));
    }
}