package com.talentboozt.s_backend.shared.mail.service;

import com.talentboozt.s_backend.shared.mail.model.NewsLatterModel;
import com.talentboozt.s_backend.shared.mail.repository.NewsLetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsLatterService {

    @Autowired
    private NewsLetterRepository newsLetterRepository;

    @Autowired
    private EmailService emailService;

    public void subscribeNewsLatter(NewsLatterModel newsLatterModel) {
        Optional<NewsLatterModel> model = newsLetterRepository.findByEmail(newsLatterModel.getEmail());

        emailService.subscribedNewsLatter(newsLatterModel.getEmail());
        if (model.isEmpty()) {
            newsLetterRepository.save(newsLatterModel);
        } else {
            NewsLatterModel model1 = model.get();
            model1.setEmail(newsLatterModel.getEmail());
            newsLetterRepository.save(model1);
        }
    }
}
