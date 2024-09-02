package com.himedias.varletserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.himedias.varletserver.dao.ReviewimgRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ReviewimgService {
    @Autowired
    private AmazonS3 amazonS3;


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private ReviewimgRepository rir;

}
