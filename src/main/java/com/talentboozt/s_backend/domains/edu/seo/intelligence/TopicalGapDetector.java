package com.talentboozt.s_backend.domains.edu.seo.intelligence;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Topical Coverage Gap Auditor.
 * Maps missing key subject areas compared to active competitors,
 * supplying targets to programmatic content pipelines.
 */
@Service
public class TopicalGapDetector {

    /**
     * Subtracts our covered terms from total competitor targets to extract search gaps.
     */
    public List<String> detectTopicalGaps(List<String> ourSubjects, List<String> competitorSubjects) {
        List<String> gapList = new ArrayList<>();
        if (competitorSubjects == null) return gapList;
        
        for (String competitorSubject : competitorSubjects) {
            String term = competitorSubject.toLowerCase().trim();
            if (ourSubjects == null || !ourSubjects.contains(term)) {
                gapList.add(competitorSubject);
            }
        }
        
        return gapList;
    }
}
