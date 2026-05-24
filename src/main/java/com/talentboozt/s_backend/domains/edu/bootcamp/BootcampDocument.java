package com.talentboozt.s_backend.domains.edu.bootcamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * Bootcamp and Cohort Management Entity.
 * Maps project cohort milestones, mentor rosters, and livestream classrooms links
 * in the MongoDB collection "bootcamps".
 */
@Document(collection = "bootcamps")
public class BootcampDocument {

    @Id
    private String id;

    private String title;
    private Date startDate;
    private Date endDate;
    private List<String> assignedMentors;
    private List<String> liveSessionLinks;
    private List<String> deadlines;
    private String communityInviteLink;

    public BootcampDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public List<String> getAssignedMentors() { return assignedMentors; }
    public void setAssignedMentors(List<String> assignedMentors) { this.assignedMentors = assignedMentors; }

    public List<String> getLiveSessionLinks() { return liveSessionLinks; }
    public void setLiveSessionLinks(List<String> liveSessionLinks) { this.liveSessionLinks = liveSessionLinks; }

    public List<String> getDeadlines() { return deadlines; }
    public void setDeadlines(List<String> deadlines) { this.deadlines = deadlines; }

    public String getCommunityInviteLink() { return communityInviteLink; }
    public void setCommunityInviteLink(String communityInviteLink) { this.communityInviteLink = communityInviteLink; }
}
