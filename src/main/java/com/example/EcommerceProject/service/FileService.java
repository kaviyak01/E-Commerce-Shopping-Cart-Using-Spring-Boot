package com.example.EcommerceProject.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public boolean uploadFileS3(MultipartFile file, int BucketType) throws IOException;
}
