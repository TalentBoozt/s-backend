package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.DTO.common.LoginMetaDTO;
import com.talentboozt.s_backend.Model.common.Login;
import com.talentboozt.s_backend.Repository.common.LoginRepository;
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

    // Record daily login
    public void recordLogin(String userId, LoginMetaDTO metadata) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        // Find login record by user ID
        Optional<Login> optionalLogin = loginRepository.findByUserId(userId);
        if (optionalLogin.isPresent()) {
            Login login = optionalLogin.get();

            // Add today's date if not already present
            if (!login.getLoginDates().contains(todayStr)) {
                login.getLoginDates().add(todayStr);
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
