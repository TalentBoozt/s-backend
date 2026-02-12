package com.talentboozt.s_backend.domains.auth.repository.mongodb;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;

import java.util.List;

public interface CredentialsRepositoryCustom {
    List<CredentialsModel> findUsersByFilters(String search, String role, String platform, Boolean filterActive);
}
