package com.talentboozt.s_backend.domains.drive.standalone.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "standalone_files")
public class StandaloneFileModel {
    @Id
    private String id;
    private String name;
    private String contentType;
    private long size;
    private String path; // relative path in storage
    private String ownerId;
    private String parentId; // for folder structure
    private boolean isDirectory;

    private Instant createdAt;
    private Instant updatedAt;

    private String storageType; // e.g., LOCAL, S3, GRIDFS
    private boolean isPublic;
}
