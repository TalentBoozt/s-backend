package com.talentboozt.s_backend.domains.finance_planning.collaboration.service;

import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.CollaborationOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConflictResolver {

    /**
     * Resolves conflicts between operations.
     * For MVP, we use Last-Write-Wins based on version and timestamp.
     * 
     * @param incoming Incoming operation
     * @param currentVersion Current version of the document/cell
     * @return true if incoming operation should be applied
     */
    public boolean shouldApply(CollaborationOperation incoming, Long currentVersion) {
        if (currentVersion == null) return true;
        
        // If incoming version is greater than current, it's definitely a newer change
        if (incoming.getVersion() > currentVersion) {
            return true;
        }
        
        // If versions are equal, we could use timestamp as tie-breaker
        // But usually, version should be strictly increasing per change
        return false;
    }
}
