package org.kupchenko.projectmanagementllm.service.impl;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Attachment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.repository.AttachmentRepository;
import org.kupchenko.projectmanagementllm.service.AttachmentService;
import org.kupchenko.projectmanagementllm.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileService fileService;

    @Override
    public void save(MultipartFile file, Task task) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String savedFilename = fileService.saveFile(file);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFilePath(savedFilename);
        attachment.setContentType(file.getContentType());

        attachmentRepository.save(attachment);
    }

    @Override
    public void delete(Attachment attachment) throws IOException {
        fileService.deleteFile(attachment.getFilePath());
        attachmentRepository.delete(attachment);
    }

    @Override
    public List<Attachment> findByTask(Task task) {
        return attachmentRepository.findByTask(task);
    }
}

