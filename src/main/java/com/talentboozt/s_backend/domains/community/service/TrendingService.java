package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.Post;
import com.talentboozt.s_backend.domains.community.repository.mongodb.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingService {

    private final PostRepository postRepository;

    // Run every 15 minutes
    @Scheduled(fixedRate = 900000)
    public void updateTrendingScores() {
        log.info("Starting trending score update...");

        // Fetch posts from the last 3 days
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Post> activePosts = postRepository.findByTimestampAfter(threeDaysAgo);

        for (Post post : activePosts) {
            double score = calculateScore(post);
            post.setTrendingScore(score);
        }

        postRepository.saveAll(Objects.requireNonNull(activePosts));
        log.info("Updated trending scores for {} posts", activePosts.size());
    }

    private double calculateScore(Post post) {
        if (post.getMetrics() == null)
            return 0.0;

        int upvotes = post.getMetrics().getUpvotes();
        int downvotes = post.getMetrics().getDownvotes();
        int comments = post.getMetrics().getComments();

        // Weighted score: Comments are worth more than simple votes
        double interactionScore = (upvotes - downvotes) + (comments * 2.0);

        // Gravity decay
        long hoursAge = Duration.between(post.getTimestamp(), LocalDateTime.now()).toHours();
        double gravity = 1.8;

        return interactionScore / Math.pow((hoursAge + 2), gravity);
    }
}
