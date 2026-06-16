package com.talentboozt.s_backend.shared.identity.repository.mongodb;

import com.talentboozt.s_backend.shared.identity.model.CredentialsModel;

import java.util.List;

public interface CredentialsRepositoryCustom {
    List<CredentialsModel> findUsersByFilters(String search, String role, String platform, Boolean filterActive);
}
