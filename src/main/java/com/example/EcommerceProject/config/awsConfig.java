package com.example.EcommerceProject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class awsConfig {

    @Value("${aws.access.key}")
    private String accesskey;

    @Value("${aws.secret.key}")
    private String secretkey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3(){

        BasicAWSCredentials credentials=new BasicAWSCredentials(accesskey,secretkey);

        return AmazonS3Client.builder().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }
}
