package com.talentboozt.s_backend.domains.portal.common.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.common.model.IssueModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssueRepository extends MongoRepository<IssueModel, String> {
}
