package com.talentboozt.s_backend.domains.common.repository;

import com.talentboozt.s_backend.domains.common.model.IssueModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssueRepository extends MongoRepository<IssueModel, String> {
}
