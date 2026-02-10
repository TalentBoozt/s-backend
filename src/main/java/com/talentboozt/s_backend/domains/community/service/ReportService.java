package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.Report;
import com.talentboozt.s_backend.domains.community.repository.ReportRepository;
import com.talentboozt.s_backend.domains.community.repository.PostRepository;
import com.talentboozt.s_backend.domains.community.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.context.event.EventListener;
import com.talentboozt.s_backend.domains.community.event.ContentCreatedEvent;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityService communityService;
    private final PostService postService;

    public Report createReport(Report report) {
        report.setStatus(Report.ReportStatus.PENDING);
        report.setTimestamp(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public void updateReportStatus(String reportId, Report.ReportStatus status) {
        reportRepository.findById(Objects.requireNonNull(reportId)).ifPresent(r -> {
            r.setStatus(status);
            reportRepository.save(r);
            if (status == Report.ReportStatus.RESOLVED) {
                handleResolvedReport(r);
            }
        });
    }

    private void handleResolvedReport(Report report) {
        switch (report.getTargetType()) {
            case POST:
                postRepository.findById(Objects.requireNonNull(report.getTargetId())).ifPresent(post -> {
                    // Logic to delete post and optionally ban user from community
                    postService.deletePost(post.getId());
                    if (post.getCommunityId() != null) {
                        communityService.banMember(post.getCommunityId(), post.getAuthorId(),
                                "Content violation: " + report.getReason());
                    }
                });
                break;
            case COMMENT:
                commentRepository.findById(Objects.requireNonNull(report.getTargetId())).ifPresent(comment -> {
                    postService.deleteComment(comment.getPostId(), comment.getId());
                    postRepository.findById(Objects.requireNonNull(comment.getPostId())).ifPresent(post -> {
                        if (post.getCommunityId() != null) {
                            communityService.banMember(post.getCommunityId(), comment.getAuthorId(),
                                    "Content violation: " + report.getReason());
                        }
                    });
                });
                break;
            case USER:
                // Global ban? For now, let's just log or handle specially
                break;
        }
    }

    @EventListener
    public void handleContentCreated(ContentCreatedEvent event) {
        String text = event.getText();
        if (text == null)
            return;

        List<String> badWords = List.of("spam", "scam", "crypto", "betting", "casino", "abuse", "offensive");
        String lowerText = text.toLowerCase();

        for (String word : badWords) {
            if (lowerText.contains(word)) {
                createReport(Report.builder()
                        .reporterId("SYSTEM")
                        .targetId(event.getTargetId())
                        .targetType(Report.ReportTargetType.valueOf(event.getType()))
                        .reason("Automated Flagging: Triggered by keyword '" + word + "'")
                        .status(Report.ReportStatus.PENDING)
                        .timestamp(LocalDateTime.now())
                        .build());
                break;
            }
        }
    }
}
