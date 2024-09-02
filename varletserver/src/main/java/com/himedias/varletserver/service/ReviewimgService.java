package com.himedias.varletserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.himedias.varletserver.dao.ReviewimgRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReviewimgService {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ReviewimgRepository rsr;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, uniqueFileName, file.getInputStream(), metadata);
        return uniqueFileName;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }

}
