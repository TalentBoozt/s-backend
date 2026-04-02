package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.thirdparty.GiftRequest;
import com.talentboozt.s_backend.domains.edu.enums.EGiftStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EGifts;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EGiftsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class EduGiftService {

    private final EGiftsRepository giftsRepository;
    private final EduEnrollmentService enrollmentService;

    public EduGiftService(EGiftsRepository giftsRepository, EduEnrollmentService enrollmentService) {
        this.giftsRepository = giftsRepository;
        this.enrollmentService = enrollmentService;
    }

    public EGifts sendGift(GiftRequest request) {
        // Typically requires checkout validation logic inside FinanceService here

        String rawCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String redeemCode = "GIFT-" + rawCode;

        EGifts gift = EGifts.builder()
                .senderId(request.getSenderId())
                .recipientEmail(request.getRecipientEmail())
                .courseId(request.getCourseId())
                .personalMessage(request.getPersonalMessage())
                .redeemCode(redeemCode)
                .status(EGiftStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        return giftsRepository.save(gift);
    }

    public EGifts redeemGift(String userId, String redeemCode) {
        EGifts gift = giftsRepository.findByRedeemCode(redeemCode)
                .orElseThrow(() -> new EduResourceNotFoundException("Invalid Gift Code: " + redeemCode));

        if (gift.getStatus() != EGiftStatus.PENDING || Instant.now().isAfter(gift.getExpiresAt())) {
            throw new EduBadRequestException("Gift Code is expired or already redeemed");
        }

        // Technically force enroll User ID into Course bypassing pay walls directly via
        // code
        // We'll mimic this by marking it successfully redeemed and updating status
        gift.setStatus(EGiftStatus.REDEEMED);
        gift.setRedeemedAt(Instant.now());
        gift.setUpdatedAt(Instant.now());
        giftsRepository.save(gift);

        enrollmentService.enrollFromGift(userId, gift.getCourseId());
        return gift;
    }
}
