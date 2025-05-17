package org.kupchenko.projectmanagementllm.service.impl;

import org.kupchenko.projectmanagementllm.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileServiceImpl implements FileService {
    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir, filename);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        return filename;
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        Files.deleteIfExists(filePath);
    }

    @Override
    public Path getFilePath(String filename) {
        return Paths.get(uploadDir, filename);
    }
}
