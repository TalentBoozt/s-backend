package com.talentboozt.s_backend.domains._public.repository;

import com.talentboozt.s_backend.domains._public.model.CtaLeadSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CtaLeadRepository extends MongoRepository<CtaLeadSubmission, String> {
}
