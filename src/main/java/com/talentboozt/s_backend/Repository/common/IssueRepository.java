package com.talentboozt.s_backend.Repository.common;

import com.talentboozt.s_backend.Model.common.IssueModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssueRepository extends MongoRepository<IssueModel, String> {
}
