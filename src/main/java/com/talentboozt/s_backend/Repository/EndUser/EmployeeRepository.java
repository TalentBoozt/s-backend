package com.talentboozt.s_backend.Repository.EndUser;

import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    List<EmployeeModel> findAllBy(Pageable pageable);
}
