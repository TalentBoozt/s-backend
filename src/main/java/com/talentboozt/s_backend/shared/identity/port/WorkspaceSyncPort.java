package com.talentboozt.s_backend.shared.identity.port;

import java.util.List;
import java.util.Map;

/**
 * Port interface for workspace synchronization.
 * Each product that manages workspaces implements this to provide
 * workspace discovery for the identity module's organization sync.
 */
public interface WorkspaceSyncPort {

    /**
     * Find all workspace IDs and names where the given user is a member.
     * Returns list of maps with "id" and "name" keys.
     */
    List<Map<String, String>> findWorkspacesForUser(String employeeId);
}
