package com.talentboozt.s_backend.domains.user.repository.mongodb;

import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    Optional<EmployeeModel> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<EmployeeModel> findAllBy(Pageable pageable);

    Page<EmployeeModel> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname,
            String lastname, Pageable pageable);

    List<EmployeeModel> findByIdNotIn(List<String> ids, Pageable pageable);
}
