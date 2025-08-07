package com.talentboozt.s_backend.domains.common.service;

import com.talentboozt.s_backend.domains.common.dto.LoginEventDTO;
import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import com.talentboozt.s_backend.domains.common.model.Login;
import com.talentboozt.s_backend.domains.common.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component
public class LoginDataMigration implements CommandLineRunner {

//    @Autowired
    private LoginRepository loginRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Login> allLogins = loginRepository.findAll();

        for (Login login : allLogins) {
            List<String> loginDates = login.getLoginDates();
            List<LoginMetaDTO> metaDataList = login.getMetaData();

            if ((login.getEvents() != null && !login.getEvents().isEmpty()) || (loginDates == null || loginDates.isEmpty())) {
                continue; // Already migrated or nothing to migrate
            }

            List<LoginEventDTO> events = new ArrayList<>();
            for (int i = 0; i < loginDates.size(); i++) {
                String date = loginDates.get(i);
                LoginEventDTO event = new LoginEventDTO();
                event.setDate(date);
                event.setLogin(true);

                // Assign corresponding metadata if exists
                if (metaDataList != null && i < metaDataList.size()) {
                    event.setMetadata(new ArrayList<>(List.of(metaDataList.get(i))));
                } else {
                    event.setMetadata(new ArrayList<>());
                }

                events.add(event);
            }

            login.setEvents(events);

            // Cleanup old fields
            login.setLoginDates(null);
            login.setMetaData(null);

            loginRepository.save(login);
        }

        System.out.println("✅ Login data migration complete.");
    }
}
