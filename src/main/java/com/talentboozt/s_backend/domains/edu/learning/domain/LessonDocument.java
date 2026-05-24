package com.talentboozt.s_backend.domains.edu.learning.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "course_lessons")
public class LessonDocument {

    @Id
    private String id;

    @Indexed
    private String moduleId;

    private String title;
    private String markdownBody;
    private String embedVideoId;
    private List<String> downloadResources;
    private String transcript;
    private List<String> tags;
    private String difficulty;
    private int xpReward = 100;

    public LessonDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMarkdownBody() { return markdownBody; }
    public void setMarkdownBody(String markdownBody) { this.markdownBody = markdownBody; }

    public String getEmbedVideoId() { return embedVideoId; }
    public void setEmbedVideoId(String embedVideoId) { this.embedVideoId = embedVideoId; }

    public List<String> getDownloadResources() { return downloadResources; }
    public void setDownloadResources(List<String> downloadResources) { this.downloadResources = downloadResources; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
