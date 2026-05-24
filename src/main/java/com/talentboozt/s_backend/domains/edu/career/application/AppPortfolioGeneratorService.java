package com.talentboozt.s_backend.domains.edu.career.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppPortfolioGeneratorService {

    public Map<String, Object> compilePortfolio(String developerName, List<String> projectsList) {
        Map<String, Object> portfolioMap = new HashMap<>();
        
        portfolioMap.put("developerName", developerName);
        portfolioMap.put("projects", projectsList);
        portfolioMap.put("githubSummary", "Active contributor with " + (projectsList != null ? projectsList.size() : 0) + " public repositories showcased.");
        portfolioMap.put("portfolioBio", "Experienced specialist showcasing professional-grade software solutions and responsive visual systems.");
        portfolioMap.put("deployedUrl", "https://" + developerName.toLowerCase().replace(" ", "") + ".talnova.site");
        
        return portfolioMap;
    }
}
