package com.talentboozt.s_backend.domains.drive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleDriveService {

    @Value("${google.drive.credentials.file.path:}")
    private String credentialsFilePath;

    private Drive driveService;

    @PostConstruct
    public void init() throws Exception {
        if (credentialsFilePath != null && !credentialsFilePath.isEmpty()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))
                    .createScoped(Collections.singleton(DriveScopes.DRIVE));

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            driveService = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Talentboozt Console Drive")
                    .build();
        }
    }

    public List<DriveFileDTO> listFiles(String parentId) throws IOException {
        String query = "'" + (parentId != null ? parentId : "root") + "' in parents and trashed = false";
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name, mimeType, size, modifiedTime, parents)")
                .execute();

        return result.getFiles().stream().map(this::mapToFileDTO).collect(Collectors.toList());
    }

    public DriveFileDTO uploadFile(MultipartFile file, String parentId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(parentId != null ? parentId : "root"));

        java.io.File tempFile = java.io.File.createTempFile("upload_", file.getOriginalFilename());
        file.transferTo(tempFile);

        FileContent mediaContent = new FileContent(file.getContentType(), tempFile);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, name, mimeType, size, modifiedTime, parents")
                .execute();

        tempFile.delete();

        return mapToFileDTO(uploadedFile);
    }

    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    public byte[] downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        return outputStream.toByteArray();
    }

    public DriveFileDTO createFolder(String folderName, String parentId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(parentId != null ? parentId : "root"));

        File folder = driveService.files().create(fileMetadata)
                .setFields("id, name, mimeType, size, modifiedTime, parents")
                .execute();

        return mapToFileDTO(folder);
    }

    private DriveFileDTO mapToFileDTO(File file) {
        return DriveFileDTO.builder()
                .id(file.getId())
                .name(file.getName())
                .mimeType(file.getMimeType())
                .size(file.getSize() != null ? file.getSize().toString() : null)
                .modifiedTime(file.getModifiedTime() != null ? file.getModifiedTime().toString() : null)
                .parents(file.getParents())
                .build();
    }
}
