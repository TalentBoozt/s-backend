package com.talentboozt.s_backend.domains.payment.repository;

import com.talentboozt.s_backend.domains.payment.model.RecordedCoursePayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordedCoursePaymentRepository
        extends MongoRepository<RecordedCoursePayment, String> {

    List<RecordedCoursePayment> findByTrainerId(String trainerId);

    List<RecordedCoursePayment> findByCourseId(String courseId);

    @Query("{ 'trainerId': ?0, 'paymentStatus': 'success' }")
    List<RecordedCoursePayment> findSuccessfulPaymentsByTrainer(String trainerId);
}

