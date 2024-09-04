package com.himedias.varletserver.controller;


import com.himedias.varletserver.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseService cs;


    @GetMapping("/getTnames/{userid}")
    public HashMap<String, Object> getTnamesByUserid(@PathVariable String userid) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("mycourse", cs.getTnamesByUserid(userid));
        return result;
    }


    @GetMapping("/getDuration/{tseq}")
    public HashMap<String, Object> getDuration(@PathVariable String tseq) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("duration", cs.getDurationByTseq(tseq));
        return result;
    }

    @GetMapping("/getMycourse/{tseq}/{userid}")
    public HashMap<String, Object> getMycourse(@PathVariable String tseq, @PathVariable String userid) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("mycourse", cs.getMycourseByTseq(tseq, userid));
        result.put("duration", cs.getDurationByTseq(tseq));
        result.put("dayschedule", cs.getDaySchedules(tseq, userid));
        return result;
    }


}
