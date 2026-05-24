package com.talentboozt.s_backend.domains.edu.career.application;

import org.springframework.stereotype.Service;

@Service
public class AppCareerCoachService {

    public String formulateAdvisorAdvice(String userFocus, String targetTrack) {
        return "Talnova Career Advisor Strategy Brief: To transition successfully into a premium " + targetTrack +
               " track, you must focus on bridging gaps in your portfolio. Start building production-ready projects in " + userFocus;
    }
}
