package com.talentboozt.s_backend.domains.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MentionService {

    private final EmployeeRepository employeeRepository;
    private static final Pattern MENTION_PATTERN_V2 = Pattern.compile("@\\[(.*?)\\]\\((.*?)\\)");
    private static final Pattern MENTION_PATTERN_LEGACY = Pattern.compile("@(\\w+)");

    /**
     * Extract mention usernames from text (Legacy support)
     * 
     * @param text The text to extract mentions from
     * @return List of mentioned usernames
     */
    public List<String> extractMentionUsernames(String text) {
        List<String> usernames = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return usernames;
        }

        Matcher matcher = MENTION_PATTERN_LEGACY.matcher(text);
        while (matcher.find()) {
            String username = matcher.group(1);
            if (!usernames.contains(username)) {
                usernames.add(username);
            }
        }
        return usernames;
    }

    /**
     * Convert usernames to user IDs (Legacy support)
     * 
     * @param usernames List of usernames
     * @return List of user IDs
     */
    public List<String> resolveUserIds(List<String> usernames) {
        List<String> userIds = new ArrayList<>();
        for (String username : usernames) {
            employeeRepository.findAll().stream()
                    .filter(emp -> username.equalsIgnoreCase(emp.getFirstname() + emp.getLastname())
                            || username.equalsIgnoreCase(emp.getFirstname()))
                    .findFirst()
                    .ifPresent(emp -> userIds.add(emp.getId()));
        }
        return userIds;
    }

    /**
     * Extract mentions and resolve to user IDs.
     * Supports both new @[Name](id) and legacy @Name formats.
     * 
     * @param text The text to extract mentions from
     * @return List of user IDs
     */
    public List<String> extractAndResolveMentions(String text) {
        List<String> userIds = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return userIds;
        }

        // 1. Try V2 Format first: @[Name](id)
        Matcher matcherV2 = MENTION_PATTERN_V2.matcher(text);
        boolean foundV2 = false;
        while (matcherV2.find()) {
            String userId = matcherV2.group(2); // The id part
            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
            foundV2 = true;
        }

        // 2. If no V2 mentions found, try Legacy fallback
        if (!foundV2) {
            List<String> usernames = extractMentionUsernames(text);
            List<String> legacyIds = resolveUserIds(usernames);
            for (String id : legacyIds) {
                if (!userIds.contains(id))
                    userIds.add(id);
            }
        }

        return userIds;
    }

    /**
     * Highlight mentions in text for display
     * 
     * @param text The text with mentions
     * @return HTML formatted text with highlighted mentions
     */
    public String highlightMentions(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Replace V2 first: @[Name](id) -> <span class="mention">@Name</span>
        String processed = MENTION_PATTERN_V2.matcher(text).replaceAll("<span class=\"mention\">@$1</span>");

        // Then legacy
        return MENTION_PATTERN_LEGACY.matcher(processed).replaceAll("<span class=\"mention\">@$1</span>");
    }
}
