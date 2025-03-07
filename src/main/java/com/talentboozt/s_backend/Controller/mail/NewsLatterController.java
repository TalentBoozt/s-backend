package com.talentboozt.s_backend.Controller.mail;

import com.talentboozt.s_backend.DTO.ApiResponse;
import com.talentboozt.s_backend.Model.mail.NewsLatterModel;
import com.talentboozt.s_backend.Service.mail.NewsLatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/news-latter")
public class NewsLatterController {

    @Autowired
    private NewsLatterService newsLatterService;

    @PutMapping("/subscribe")
    public ResponseEntity<ApiResponse> subscribeNewsLatter(@RequestBody NewsLatterModel newsLatterModel) {
        try {
            newsLatterService.subscribeNewsLatter(newsLatterModel);
            return ResponseEntity.ok(new ApiResponse("Email sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Something went wrong. Please try again."));
        }
    }
}
