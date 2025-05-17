package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Attachment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AttachmentService {
    void save(MultipartFile file, Task task) throws IOException;

    void delete(Attachment attachment) throws IOException;

    List<Attachment> findByTask(Task task);
}
