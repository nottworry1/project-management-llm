package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Attachment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTask(Task task);
}
