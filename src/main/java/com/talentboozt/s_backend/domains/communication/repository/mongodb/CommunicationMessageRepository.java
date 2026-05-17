package com.talentboozt.s_backend.domains.communication.repository.mongodb;

import com.talentboozt.s_backend.domains.communication.model.CommunicationMessageModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommunicationMessageRepository extends MongoRepository<CommunicationMessageModel, String> {
    List<CommunicationMessageModel> findByThreadIdOrderByTimestampAsc(String threadId);
}
