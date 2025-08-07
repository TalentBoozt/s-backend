package com.talentboozt.s_backend.domains.common.service;

import com.talentboozt.s_backend.domains.common.dto.LocationCordDTO;
import com.talentboozt.s_backend.domains.common.dto.LoginEventDTO;
import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import com.talentboozt.s_backend.domains.common.model.Login;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.common.repository.LoginRepository;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private AmbassadorPointService pointService;

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    // Record daily login
    public void recordLogin(String userId, LoginMetaDTO metadata) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        if (metadata == null) {
            metadata = new LoginMetaDTO();
            metadata.setPlatform("Unknown");
            metadata.setReferrer("Unknown");
            metadata.setPromotion("Unknown");
            metadata.setProvider("Unknown");
            metadata.setUserAgent("Unknown");
            metadata.setLanguage("Unknown");
            metadata.setLanguages("Unknown");
            metadata.setPlatformDetails("Unknown");
            metadata.setHardwareConcurrency("Unknown");
            metadata.setDeviceMemory("Unknown");
            metadata.setCookiesEnabled("Unknown");
            metadata.setOnlineStatus("Unknown");
            metadata.setLocation(new LocationCordDTO());
        }

        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        Login login = optionalLogin.orElseGet(() -> {
            Login newLogin = new Login();
            newLogin.setUserId(userId);
            return newLogin;
        });

        List<LoginEventDTO> events = login.getEvents();

        Optional<LoginEventDTO> todayEventOpt = events.stream()
                .filter(e -> e.getDate().equals(todayStr))
                .findFirst();

        if (todayEventOpt.isPresent()) {
            LoginEventDTO todayEvent = todayEventOpt.get();
            if (!todayEvent.isLogin()) {
                todayEvent.setLogin(true);
                pointService.handleDailyLogin(userId); // Only grant points once per day
            }
            todayEvent.getMetadata().add(metadata);
        } else {
            LoginEventDTO newEvent = new LoginEventDTO();
            newEvent.setDate(todayStr);
            newEvent.setLogin(true);
            newEvent.setMetadata(new ArrayList<>(List.of(metadata)));
            events.add(newEvent);

            pointService.handleDailyLogin(userId);
        }

        login.setEvents(events);
        loginRepository.save(login);
    }

    public Map<String, Map<String, Object>> getAllEventsByYear(String userId, int year) {
        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        if (optionalLogin.isEmpty()) {
            return new HashMap<>();
        }

        Login login = optionalLogin.get();
        String yearPrefix = year + "-";

        Map<String, Map<String, Object>> eventsMap = new HashMap<>();

        for (LoginEventDTO event : login.getEvents()) {
            if (event.getDate().startsWith(yearPrefix)) {
                Map<String, Object> dailyData = new HashMap<>();
                dailyData.put("login", event.isLogin());
                dailyData.put("taskCompletions", event.getTaskCompletions());
                dailyData.put("referrals", event.getReferrals());
                dailyData.put("redeems", event.getRedeems());
                dailyData.put("courseParticipation", event.getCourseParticipation());
                dailyData.put("courseConduct", event.getCourseConduct());

                eventsMap.put(event.getDate(), dailyData);
            }
        }

        return eventsMap;
    }

    public void recordEvent(String userId, String type, int count) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        Login login = optionalLogin.orElseGet(() -> {
            Login newLogin = new Login();
            newLogin.setUserId(userId);
            return newLogin;
        });

        List<LoginEventDTO> events = login.getEvents();

        LoginEventDTO event = events.stream()
                .filter(e -> e.getDate().equals(todayStr))
                .findFirst()
                .orElseGet(() -> {
                    LoginEventDTO newEvent = new LoginEventDTO();
                    newEvent.setDate(todayStr);
                    newEvent.setMetadata(new ArrayList<>());
                    events.add(newEvent);
                    return newEvent;
                });

        switch (type) {
            case "taskCompletions" -> event.setTaskCompletions(event.getTaskCompletions() + count);
            case "referrals" -> event.setReferrals(event.getReferrals() + count);
            case "redeems" -> event.setRedeems(event.getRedeems() + count);
            case "courseParticipation" -> event.setCourseParticipation(event.getCourseParticipation() + count);
            case "courseConduct" -> event.setCourseConduct(event.getCourseConduct() + count);
            default -> throw new IllegalArgumentException("Unsupported event type: " + type);
        }

        login.setEvents(events);
        loginRepository.save(login);
    }

    // Fetch login dates for a user and year
    public List<String> getLoginDatesForYear(String userId, int year) {
        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        if (optionalLogin.isPresent()) {
            Login login = optionalLogin.get();
            String yearPrefix = year + "-";
            List<String> yearLogins = new ArrayList<>();
            for (String date : login.getEvents().stream().map(LoginEventDTO::getDate).toList()) {
                if (date.startsWith(yearPrefix)) {
                    yearLogins.add(date);
                }
            }
            return yearLogins;
        }
        return new ArrayList<>();
    }
}
