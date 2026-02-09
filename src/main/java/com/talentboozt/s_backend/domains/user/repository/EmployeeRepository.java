package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    List<EmployeeModel> findAllBy(Pageable pageable);

    Optional<EmployeeModel> findByEmail(String email);

    List<EmployeeModel> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname,
        String lastname, Pageable pageable);
}
