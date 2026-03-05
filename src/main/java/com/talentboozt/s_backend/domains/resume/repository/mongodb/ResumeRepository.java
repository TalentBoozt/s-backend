package com.talentboozt.s_backend.domains.resume.repository.mongodb;

import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<ResumeModel, String> {

    /** All non-deleted resumes for an SSO user, newest first */
    List<ResumeModel> findByEmployeeIdAndDeletedFalseOrderByUpdatedAtDesc(String employeeId);

    /** Fetch a specific resume that belongs to an employee (guards against access-control bypass) */
    Optional<ResumeModel> findByIdAndEmployeeIdAndDeletedFalse(String id, String employeeId);

    /** Existence check for soft-delete guard */
    boolean existsByIdAndEmployeeIdAndDeletedFalse(String id, String employeeId);

    /** Count resumes for a user */
    long countByEmployeeIdAndDeletedFalse(String employeeId);
}
