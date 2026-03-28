package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class EduAdminService {

    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EUserRepository userRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EReportsRepository reportsRepository;

    public EduAdminService(ECoursesRepository coursesRepository,
                           EEnrollmentsRepository enrollmentsRepository,
                           EUserRepository userRepository,
                           ETransactionsRepository transactionsRepository,
                           EReportsRepository reportsRepository) {
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.userRepository = userRepository;
        this.transactionsRepository = transactionsRepository;
        this.reportsRepository = reportsRepository;
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
        stats.put("pendingModeration", reportsRepository.findByStatus(EReportStatus.PENDING).size());
        stats.put("systemHealth", 100);

        return stats;
    }

    public Page<EUser> getUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                search, search, pageable);
    }

    public void updateUserStatus(String userId, Boolean banned, Boolean active, String reason) {
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (banned != null) {
            user.setIsBanned(banned);
            if (banned) {
                user.setBanReason(reason);
            }
        }
        if (active != null) {
            user.setIsActive(active);
        }
        
        userRepository.save(user);
    }
}
