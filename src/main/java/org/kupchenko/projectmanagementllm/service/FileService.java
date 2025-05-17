package org.kupchenko.projectmanagementllm.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    String saveFile(MultipartFile file) throws IOException;

    void deleteFile(String filename) throws IOException;

    Path getFilePath(String filename);
}
