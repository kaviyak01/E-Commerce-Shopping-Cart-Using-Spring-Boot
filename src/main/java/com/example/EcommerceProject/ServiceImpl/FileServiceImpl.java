package com.example.EcommerceProject.ServiceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.EcommerceProject.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;



@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;


    @Override
    public boolean uploadFileS3(MultipartFile file, int BucketType) throws IOException {

        String bucketName=null;
        if(BucketType==1)
        {
            bucketName=categoryBucket;
        }
        else if(BucketType==2)
        {
            bucketName=productBucket;
        }
        else {
            bucketName=profileBucket;
        }

        String fileName=file.getOriginalFilename();

        InputStream inputStream=file.getInputStream();

        ObjectMetadata objectMetadata=new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest=new PutObjectRequest(bucketName,fileName,inputStream,objectMetadata);

        PutObjectResult saveData=amazonS3.putObject(putObjectRequest);

        if(!ObjectUtils.isEmpty(saveData))
        {
            return true;
        }


        return false;
    }
}
