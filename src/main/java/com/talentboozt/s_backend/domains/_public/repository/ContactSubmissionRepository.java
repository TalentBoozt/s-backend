package com.talentboozt.s_backend.domains._public.repository;

import com.talentboozt.s_backend.domains._public.model.ContactSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactSubmissionRepository extends MongoRepository<ContactSubmission, String> {
}
