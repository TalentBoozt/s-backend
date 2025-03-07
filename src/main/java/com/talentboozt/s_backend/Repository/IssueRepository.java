package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.IssueModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssueRepository extends MongoRepository<IssueModel, String> {
}
