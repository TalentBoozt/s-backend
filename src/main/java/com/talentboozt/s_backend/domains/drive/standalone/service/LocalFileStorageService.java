package com.talentboozt.s_backend.domains.drive.standalone.service;

import com.talentboozt.s_backend.domains.drive.standalone.model.StandaloneFileModel;
import com.talentboozt.s_backend.domains.drive.standalone.repository.mongodb.StandaloneFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService {

    @Value("${storage.local.path:storage/drive}")
    private String baseStoragePath;

    @Autowired
    private StandaloneFileRepository fileRepository;

    public StandaloneFileModel uploadFile(MultipartFile file, String parentId, String ownerId) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileId = UUID.randomUUID().toString();

        // Define physical path
        Path userPath = Paths.get(baseStoragePath, ownerId);
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        Path filePath = userPath.resolve(fileId + "_" + fileName);
        Files.copy(file.getInputStream(), filePath);

        StandaloneFileModel model = new StandaloneFileModel();
        model.setName(fileName);
        model.setOwnerId(ownerId);
        model.setParentId(parentId != null ? parentId : "root");
        model.setContentType(file.getContentType());
        model.setSize(file.getSize());
        model.setPath(filePath.toString());
        model.setDirectory(false);
        model.setStorageType("LOCAL");
        model.setCreatedAt(Instant.now());
        model.setUpdatedAt(Instant.now());

        return fileRepository.save(model);
    }

    public StandaloneFileModel createFolder(String folderName, String parentId, String ownerId) {
        StandaloneFileModel model = new StandaloneFileModel();
        model.setName(folderName);
        model.setOwnerId(ownerId);
        model.setParentId(parentId != null ? parentId : "root");
        model.setDirectory(true);
        model.setStorageType("LOCAL");
        model.setCreatedAt(Instant.now());
        model.setUpdatedAt(Instant.now());

        return fileRepository.save(model);
    }

    public List<StandaloneFileModel> listFiles(String parentId, String ownerId) {
        return fileRepository.findByParentIdAndOwnerId(parentId != null ? parentId : "root", ownerId);
    }

    public void deleteFile(String id, String ownerId) throws IOException {
        StandaloneFileModel model = fileRepository.findById(id).orElse(null);
        if (model != null && model.getOwnerId().equals(ownerId)) {
            if (!model.isDirectory() && model.getPath() != null) {
                Files.deleteIfExists(Paths.get(model.getPath()));
            }
            fileRepository.delete(model);
        }
    }
}
