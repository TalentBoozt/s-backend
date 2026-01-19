package com.talentboozt.s_backend.domains.auth.repository;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class CredentialsRepositoryCustomImpl implements CredentialsRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<CredentialsModel> findUsersByFilters(String search, String role, String platform, Boolean filterActive) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstname").regex(search, "i"),
                    Criteria.where("lastname").regex(search, "i"),
                    Criteria.where("email").regex(search, "i")
            );
            criteriaList.add(searchCriteria);
        }

        if (role != null && !role.isBlank()) {
            criteriaList.add(Criteria.where("roles").regex(role, "i"));
        }

        if (platform != null && !platform.isBlank()) {
            criteriaList.add(Criteria.where("accessedPlatforms").regex(platform, "i"));
        }

        if (filterActive != null) {
            criteriaList.add(Criteria.where("active").is(filterActive));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(Objects.requireNonNull(criteriaList.toArray(new Criteria[0]))));
        }

        return mongoTemplate.find(query, CredentialsModel.class);
    }
}
