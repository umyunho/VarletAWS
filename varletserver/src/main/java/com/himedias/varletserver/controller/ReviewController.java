package com.himedias.varletserver.controller;

import com.himedias.varletserver.dto.Paging;
import com.himedias.varletserver.entity.Review;
import com.himedias.varletserver.entity.Reviewpreview;
import com.himedias.varletserver.entity.ReviewSummary;
import com.himedias.varletserver.entity.Reviewimg;
import com.himedias.varletserver.service.ReviewService;
import com.himedias.varletserver.service.ReviewimgService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService rs;

    @Autowired
    private ReviewimgService ris;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 사용자가 리뷰를 작성하고 이미지를 업로드할 때 호출되는 메소드입니다.
    @PostMapping("/writeReview")
    public ResponseEntity<Map<String, Object>> writeReview(
            @RequestParam("title") String title,  // 리뷰 제목을 요청 파라미터로 받습니다.
            @RequestParam("content") String content,  // 리뷰 내용을 요청 파라미터로 받습니다.
            @RequestParam("userid") String userid,  // 사용자 ID를 요청 파라미터로 받습니다.
            @RequestParam(value = "reviewimg", required = false) MultipartFile[] reviewimgs) {  // 선택적으로 이미지를 업로드할 수 있습니다.

        Map<String, Object> result = new HashMap<>();  // 결과를 저장할 맵을 초기화합니다.
        try {
            Review review = new Review();  // 새로운 Review 객체를 생성합니다.
            review.setTitle(title);  // 리뷰 제목을 설정합니다.
            review.setContent(content);  // 리뷰 내용을 설정합니다.
            review.setUserid(userid);  // 사용자 ID를 설정합니다.

            // 리뷰를 데이터베이스에 저장하고, 저장된 리뷰 객체를 반환받습니다.
            Review savedReview = rs.writeReview(review);

            if (reviewimgs != null && reviewimgs.length > 0) {  // 만약 이미지가 업로드되었다면,
                List<String> filenames = new ArrayList<>();  // 업로드된 파일명을 저장할 리스트를 초기화합니다.

                for (MultipartFile file : reviewimgs) {  // 업로드된 각 파일에 대해
                    try {
                        // S3에 파일을 업로드하고, 업로드된 파일명을 반환받습니다.
                        String uploadedFilename = ris.uploadFile(file);
                        filenames.add(uploadedFilename);  // 업로드된 파일명을 리스트에 추가합니다.
                    } catch (IOException e) {  // 파일 업로드 중 예외가 발생하면,
                        result.put("status", "error");  // 결과 맵에 에러 상태를 저장하고,
                        result.put("message", "Failed to upload files: " + e.getMessage());  // 에러 메시지를 저장합니다.
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);  // 500 에러와 함께 응답을 반환합니다.
                    }
                }

                // 업로드된 파일명을 기반으로 Reviewimg 객체 리스트를 생성합니다.
                List<Reviewimg> reviewImgList = filenames.stream().map(filename -> {
                    Reviewimg reviewImg = new Reviewimg();
                    reviewImg.setIpath("https://" + bucketName + ".s3.amazonaws.com/" + filename);  // S3에 저장된 파일의 URL을 설정합니다.
                    reviewImg.setIname(filename);  // 파일명을 설정합니다.
                    reviewImg.setRseq(savedReview.getRseq());  // Review와 연관된 rseq(리뷰 ID)를 설정합니다.
                    return reviewImg;
                }).collect(Collectors.toList());

                // 저장된 리뷰에 이미지 리스트를 설정합니다.
                savedReview.setReviewimg(reviewImgList);
                rs.writeReview(savedReview);  // 리뷰를 다시 저장하여 이미지를 포함한 상태로 업데이트합니다.
            }

            // 성공 상태와 함께 저장된 리뷰 객체를 결과에 담아 반환합니다.
            result.put("status", "success");
            result.put("review", savedReview);
            return ResponseEntity.ok(result);
        } catch (Exception e) {  // 예외가 발생하면
            result.put("status", "error");  // 결과 맵에 에러 상태를 저장하고,
            result.put("message", "Failed to write review: " + e.getMessage());  // 에러 메시지를 저장합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);  // 500 에러와 함께 응답을 반환합니다.
        }
    }




    @PostMapping("/updateReview/{rseq}")
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable int rseq,  // URL 경로에서 리뷰의 고유 번호(rseq)를 받아옵니다.
            @RequestParam("title") String title,  // 리뷰의 제목을 받아옵니다.
            @RequestParam("content") String content,  // 리뷰의 내용을 받아옵니다.
            @RequestParam(value = "reviewimg", required = false) List<MultipartFile> reviewimgs) {  // 업로드할 이미지 파일 리스트를 받아옵니다.

        Map<String, Object> result = new HashMap<>();  // 응답 데이터를 저장할 맵을 생성합니다.
        try {
            // rseq에 해당하는 리뷰를 데이터베이스에서 찾습니다. 없으면 예외를 발생시킵니다.
            Review review = rs.findById(rseq).orElseThrow(() -> new RuntimeException("Review not found"));

            // 리뷰의 제목과 내용을 새로 받아온 값으로 업데이트합니다.
            review.setTitle(title);
            review.setContent(content);

            // 새로운 이미지 파일이 업로드된 경우 이를 처리합니다.
            if (reviewimgs != null && !reviewimgs.isEmpty()) {
                List<String> filenames = new ArrayList<>();

                for (MultipartFile file : reviewimgs) {
                    try {
                        // 각 파일을 S3에 업로드하고, 업로드된 파일명을 리스트에 추가합니다.
                        String uploadedFilename = ris.uploadFile(file);
                        filenames.add(uploadedFilename);
                    } catch (IOException e) {
                        // 업로드 과정에서 오류가 발생하면, 오류 메시지를 응답으로 반환합니다.
                        result.put("status", "error");
                        result.put("message", "Failed to upload files during review update: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
                    }
                }

                // 업로드된 파일명을 기반으로 Reviewimg 객체 리스트를 생성합니다.
                List<Reviewimg> reviewImgList = filenames.stream().map(filename -> {
                    Reviewimg reviewImg = new Reviewimg();
                    // S3에 저장된 파일의 URL을 설정합니다.
                    reviewImg.setIpath("https://" + bucketName + ".s3.amazonaws.com/" + filename);
                    reviewImg.setIname(filename);  // 파일명을 설정합니다.
                    return reviewImg;
                }).collect(Collectors.toList());

                // 리뷰 객체에 새로 업로드된 이미지 리스트를 설정합니다.
                review.setReviewimg(reviewImgList);
            }

            // 수정된 리뷰 정보를 데이터베이스에 저장합니다.
            rs.updateReview(rseq, review);

            result.put("status", "success");  // 성공 상태를 응답에 추가합니다.
            return ResponseEntity.ok(result);  // 성공 응답을 반환합니다.
        } catch (Exception e) {
            // 예외가 발생하면 오류 상태와 메시지를 응답에 추가하고 반환합니다.
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }




    @GetMapping("/getReviewView/{rseq}")
    public ResponseEntity<Map<String, Object>> getReviewView(@PathVariable Integer rseq) {
        Map<String, Object> result = new HashMap<>();
        try {
            Review review = rs.findById(rseq).orElseThrow(() -> new RuntimeException("Review not found"));
            rs.incrementReadcount(rseq); // 조회수 증가

            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("rseq", review.getRseq());
            reviewData.put("userid", review.getUserid());
            reviewData.put("title", review.getTitle());
            reviewData.put("content", review.getContent());
            reviewData.put("indate", review.getIndate());
            reviewData.put("readcount", review.getReadcount());
            reviewData.put("reviewimg", review.getReviewimg().stream()
                    .map(img -> {
                        Map<String, Object> imgData = new HashMap<>();
                        imgData.put("iseq", img.getIseq());
                        imgData.put("rseq", img.getRseq());
                        imgData.put("ipath", img.getIpath());
                        imgData.put("iname", img.getIname());
                        return imgData;
                    })
                    .collect(Collectors.toList()));

            result.put("status", "success");
            result.put("review", reviewData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }



    @DeleteMapping("/reviewDelete/{rseq}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Integer rseq) {
        Map<String, Object> result = new HashMap<>();
        try {
            // rseq에 해당하는 리뷰를 데이터베이스에서 찾습니다. 없으면 예외를 발생시킵니다.
            Review review = rs.findById(rseq).orElseThrow(() -> new RuntimeException("Review not found"));

            // 리뷰에 이미지가 포함되어 있는지 확인합니다.
            if (review.getReviewimg() != null && !review.getReviewimg().isEmpty()) {
                // 각 이미지를 S3에서 삭제합니다.
                for (Reviewimg img : review.getReviewimg()) {
                    // S3에서 해당 파일을 삭제합니다.
                    ris.deleteFile(img.getIname());
                }
            }

            // 데이터베이스에서 해당 리뷰를 삭제합니다.
            rs.deleteReview(rseq);

            // 성공 메시지를 응답에 추가합니다.
            result.put("status", "success");
            return ResponseEntity.ok(result);  // 성공 응답을 반환합니다.
        } catch (Exception e) {
            // 예외가 발생하면 오류 상태와 메시지를 응답에 추가하고 반환합니다.
            result.put("status", "error");
            result.put("message", "Failed to delete review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }


    /*@GetMapping("/api/review/reviewList")
    public ResponseEntity<List<Review>> getReviewsByUser (@RequestParam String userid){
        try {
            List<Review> reviews = rs.getReviewsByUserId(userid);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/

    @GetMapping("/reviewList/{page}")
    public HashMap<String, Object> reviewList(@PathVariable("page") int page) {
        HashMap<String, Object> result = new HashMap<>();
        Paging paging = new Paging();
        paging.setPage(page);
        paging.setDisplayRow(10);
        paging.setSort(Sort.by(Sort.Order.desc("rseq")));

        Page<ReviewSummary> reviewPage = rs.getReviewList(paging);

        paging.setTotalCount((int) reviewPage.getTotalElements());

        paging.calPaging();

        result.put("reviewList", reviewPage.getContent());
        result.put("paging", paging);

        return result;
    }

    @GetMapping("/reviewSearch")
    public ResponseEntity<Map<String, Object>> reviewSearch(@RequestParam("query") String query) {
        List<Review> reviewList = rs.reviewSearch(query);
        Map<String, Object> result = new HashMap<>();
        result.put("reviewList", reviewList);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reviewPreviewSearch")
    public ResponseEntity<Map<String, Object>> reviewPreviewSearch(@RequestParam("query") String query) {
        List<Reviewpreview> reviewpreviewList = rs.reviewPreviewSearch(query);
        Map<String, Object> result = new HashMap<>();
        result.put("reviewList", reviewpreviewList);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/userReviews/{userid}")
    public HashMap<String, Object> userReviews(@PathVariable("userid") String userid) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<ReviewSummary> reviewList = rs.getReviewsByUser(userid); // 사용자에 대한 모든 리뷰를 가져옴
            result.put("reviewList", reviewList);
            result.put("status", "success");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/reviewPreviewList/{page}")
    public HashMap<String, Object> reviewPreviewList(@PathVariable("page") int page) {
        HashMap<String, Object> result = new HashMap<>();
        Paging paging = new Paging();
        paging.setPage(page);
        paging.setDisplayRow(10);
        paging.setSort(Sort.by(Sort.Order.desc("rseq")));

        Page<Reviewpreview> reviewPreviewPage = rs.getReviewPreviewList(paging);

        paging.setTotalCount((int) reviewPreviewPage.getTotalElements());

        paging.calPaging();

        result.put("reviewList", reviewPreviewPage.getContent());
        result.put("paging", paging);

        return result;
    }
}