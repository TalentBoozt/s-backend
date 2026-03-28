package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.repository.mongodb.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EduAdminService {

    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EUserRepository userRepository;
    private final ETransactionsRepository transactionsRepository;

    public EduAdminService(ECoursesRepository coursesRepository,
                           EEnrollmentsRepository enrollmentsRepository,
                           EUserRepository userRepository,
                           ETransactionsRepository transactionsRepository) {
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.userRepository = userRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCourses", coursesRepository.count());
        stats.put("totalEnrollments", enrollmentsRepository.count());
        stats.put("totalUsers", userRepository.count());
        
        // Sum total revenue from transactions
        double totalRevenue = transactionsRepository.findAll().stream()
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        stats.put("totalRevenue", totalRevenue);
        
        double platformEarnings = transactionsRepository.findAll().stream()
                .mapToDouble(t -> t.getPlatformFee() != null ? t.getPlatformFee() : 0.0)
                .sum();
        stats.put("platformEarnings", platformEarnings);

        return stats;
    }
}
