package com.talentboozt.s_backend.domains.common.service;

import com.talentboozt.s_backend.domains.common.dto.LocationCordDTO;
import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import com.talentboozt.s_backend.domains.common.model.Login;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.common.repository.LoginRepository;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        boolean isNewLogin = false;

        // Find login record by user ID
        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        if (optionalLogin.isPresent()) {
            Login login = optionalLogin.get();

            // Add today's date if not already present
            if (!login.getLoginDates().contains(todayStr)) {
                login.getLoginDates().add(todayStr);
                login.getMetaData().add(metadata);
                loginRepository.save(login);
                isNewLogin = true;
            }

            if (userId.equals("unknown")) {
                login.setLoginDates(login.getLoginDates());
                login.getMetaData().add(metadata);
                loginRepository.save(login);
            }
        } else {
            // Create new login record if not exists
            Login newLogin = new Login();
            newLogin.setUserId(userId);
            List<String> dates = new ArrayList<>();
            List<LoginMetaDTO> newMetaData = new ArrayList<>();
            dates.add(todayStr);
            newMetaData.add(metadata);
            newLogin.setMetaData(newMetaData);
            newLogin.setLoginDates(dates);
            loginRepository.save(newLogin);
            isNewLogin = true;
        }

        if (isNewLogin) {
            pointService.handleDailyLogin(userId);
        }
    }

    // Fetch login dates for a user and year
    public List<String> getLoginDatesForYear(String userId, int year) {
        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        if (optionalLogin.isPresent()) {
            Login login = optionalLogin.get();
            String yearPrefix = year + "-";
            List<String> yearLogins = new ArrayList<>();
            for (String date : login.getLoginDates()) {
                if (date.startsWith(yearPrefix)) {
                    yearLogins.add(date);
                }
            }
            return yearLogins;
        }
        return new ArrayList<>();
    }
}
