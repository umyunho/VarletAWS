package com.himedias.varletserver.controller;

import com.himedias.varletserver.dto.Paging;
import com.himedias.varletserver.entity.Qna;
import com.himedias.varletserver.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    QnaService qs;

    @GetMapping("")
    public Map<String, Object> qnalist(
            @PageableDefault(sort = "indate", direction = Sort.Direction.DESC)
            Pageable pageable,

            @RequestParam(value = "userid", required = false)
            String userid
    ) {
        // 인자(RequestParam) = 프론트에서 넘겨주는 값

        // QnA 페이지 객체를 담을 변수를 선언
        Page<Qna> qnaPage;
        if (userid != null && !userid.isEmpty()) {
            // userid가 null이 아니고, 비어있지 않으면 userid를 통해서 qna 목록을 가져옴
            qnaPage = qs.getQnaList(pageable, userid);
        } else {
            // 아니면 모든 qna 목록을 가져옴
            qnaPage = qs.getQnaList(pageable);
        }

        // Paging 객체로 변환
        Paging paging = new Paging();
        paging.setPage(qnaPage.getNumber() + 1);
        paging.setDisplayRow(20);
        paging.setTotalCount((int) qnaPage.getTotalElements());
        paging.calPaging();

        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("qnaList", qnaPage.getContent());
        result.put("paging", paging);

        return result;
    }

    @GetMapping("/qnaList/{page}")
    public HashMap<String, Object> qnalist(@PathVariable("page") int page) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        Paging paging = new Paging();
        paging.setPage(page);
        paging.setDisplayRow(20);

        paging.setSort(Sort.by(Sort.Order.desc("indate")));
        Page<Qna> qnaPage = qs.getQnaList(paging);


        paging.setTotalCount((int) qnaPage.getTotalElements());
        paging.calPaging();

        // Prepare response
        result.put("qnaList", qnaPage.getContent());
        result.put("paging", paging);

        return result;

    }

    @PostMapping("/writeQna")
    public HashMap<String, Object> writeQna(@RequestBody Qna qna) {
        qs.writeQna(qna);
        return null;
    }

    @GetMapping("/getQnaView/{qseq}")
    public HashMap<String, Object> getQnaView(@PathVariable("qseq") int qseq) {
        HashMap<String, Object> result = new HashMap<>();
        Qna qna = qs.getQnaView(qseq);
        result.put("qna", qna);
        return result;
    }

    @PostMapping("/passCheck")
    public HashMap<String, Object> passCheck(@RequestParam("qseq") int qseq,
                                             @RequestParam("inputPass") String inputPass) {
        HashMap<String, Object> result = new HashMap<>();
        Qna qna = qs.getQnaView(qseq);
        if (qna.getPass().equals(inputPass)) result.put("msg", "OK");
        else result.put("msg", "FAIL");

        return result;
    }

    @DeleteMapping("/qnaDelete/{qseq}")
    public HashMap<String, Object> qnaDelete(@PathVariable("qseq") int qseq) {
        qs.deleteQna(qseq);
        return null;
    }


}