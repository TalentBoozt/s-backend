package com.talentboozt.s_backend.domains._public.controller;

import com.talentboozt.s_backend.domains._public.service.PublicService;
import com.talentboozt.s_backend.domains.com_courses.dto.CourseMissedNotify;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @PostMapping("/add/batch-notification/{courseId}")
    public ResponseEntity<ApiResponse> addBatchNotification(@PathVariable String courseId, @RequestBody CourseMissedNotify courseMissedNotify) {
        return publicService.addNewBatchNotification(courseId, courseMissedNotify);
    }
}
