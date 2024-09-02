package com.himedias.varletserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.himedias.varletserver.dao.ImageRepository;
import com.himedias.varletserver.entity.Image;
import com.himedias.varletserver.entity.Member;
import com.himedias.varletserver.entity.Rcrecommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Service
public class ImageService {


    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ImageRepository ir;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public Image uploadImage(Member member, Rcrecommend rcRecommend, MultipartFile file, Image.ImageType imageType) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String s3FileName = UUID.randomUUID() + "_" + originalFilename;

        // S3에 파일 업로드
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucketName, s3FileName, file.getInputStream(), metadata);

        // 파일의 S3 URL 생성
        String filePath = amazonS3.getUrl(bucketName, s3FileName).toString();

        // Image 엔티티 생성 및 저장
        Image image = new Image(member, rcRecommend, originalFilename, filePath, imageType);
        return ir.save(image);
    }


    public void saveFiles(MultipartFile[] files, Member member, Rcrecommend rcrecommend, HashMap<String, String> allParams) throws IOException {
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String imageTypeStr = allParams.get("image_type_" + i);

            // 이미지 타입 변환
            Image.ImageType imageType;
            try {
                imageType = Image.ImageType.valueOf(imageTypeStr);
            } catch (IllegalArgumentException e) {
                imageType = Image.ImageType.기타; // 기본값 설정
            }

            uploadImage(member, rcrecommend, file, imageType);
        }
    }

}
