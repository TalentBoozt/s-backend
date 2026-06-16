package com.talentboozt.s_backend.shared.identity.port;

import java.util.Optional;

/**
 * Port interface for looking up user identity data.
 * Products should depend on this interface rather than directly
 * importing CredentialsModel/EmployeeModel from the identity module.
 */
public interface UserLookupPort {

    /**
     * Check if a user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Get user's employee ID by their credentials email.
     */
    Optional<String> getEmployeeIdByEmail(String email);

    /**
     * Get user's company ID by employee ID.
     */
    Optional<String> getCompanyIdByEmployeeId(String employeeId);
}
